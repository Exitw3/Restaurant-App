package com.codegym.service.customer;

import com.codegym.exception.DataInputException;
import com.codegym.model.Avatar;
import com.codegym.model.Customer;
import com.codegym.model.LocationRegion;
import com.codegym.model.dto.AvatarDTO;
import com.codegym.model.dto.CustomerAvartasDTO;
import com.codegym.model.enums.FileType;
import com.codegym.repository.AvatarRepository;
import com.codegym.repository.CustomerRepository;
import com.codegym.repository.LocationRegionRepositiry;
import com.codegym.service.upload.UploadService;
import com.codegym.utils.UploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LocationRegionRepositiry locationRegionRepositiry;

    @Autowired
    private AvatarRepository avatarRepository;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private UploadUtils uploadUtils;

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }


    @Override
    public Customer getById(Long id) {
        return null;
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Customer save(Customer customer) {
        locationRegionRepositiry.save(customer.getLocationRegion());
        return customerRepository.save(customer);
    }

    @Override
    public void remove(Long id) {

    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public Optional<Customer> findByEmailAndIdIsNot(String email, Long id) {
        return customerRepository.findByEmailAndIdIsNot(email,id);
    }

    @Override
    public List<Customer> findAllByDeletedIsFalse() {
        return customerRepository.findAllByDeletedIsFalse();
    }

    @Override
    public List<CustomerAvartasDTO> getAllCustomersAvartaDTO() {
        return customerRepository.getAllCustomersAvartaDTO();
    }

    @Override
    public CustomerAvartasDTO saveWithAvatar(Customer customer, MultipartFile avatarFile) {

        LocationRegion locationRegion = customer.getLocationRegion();
        locationRegion.setId(0L);
        LocationRegion newLocationRegion = locationRegionRepositiry.save(locationRegion);

        customer.setLocationRegion(newLocationRegion);

        Customer newCustomer = customerRepository.save(customer);

        Avatar avatar = new Avatar();

        String fileType = avatarFile.getContentType();

        assert fileType != null;

        fileType = fileType.substring(0, 5);

        avatar.setFileType(fileType);
        avatar.setCustomer(newCustomer);

        Avatar newAvatar = avatarRepository.save(avatar);

        if (fileType.equals(FileType.IMAGE.getValue())) {
            uploadAndSaveProductImage(avatarFile, newCustomer, newAvatar);
        }

        CustomerAvartasDTO customerAvartasDTO = newCustomer.toCustomerAvartasDTO();
        customerAvartasDTO.setAvatarDTO(newAvatar.toAvatarDTO());

        return customerAvartasDTO;
    }

    @Override
    public CustomerAvartasDTO updateWithAvatar(Customer customer, MultipartFile avatarFile, AvatarDTO avatarDTO) {

        LocationRegion locationRegion = customer.getLocationRegion();

        LocationRegion newLocationRegion = locationRegionRepositiry.save(locationRegion);

        customer.setLocationRegion(newLocationRegion);

        Customer newCustomer = customerRepository.save(customer);

        Avatar avatar = new Avatar();

        String fileType = avatarFile.getContentType();

        assert fileType != null;

        fileType = fileType.substring(0, 5);

        avatar.setFileType(fileType);
        avatar.setCustomer(newCustomer);
        Avatar newAvatar = avatarRepository.save(avatar);


        if (avatarDTO.getId() == null) {
            if (fileType.equals(FileType.IMAGE.getValue())) {
                uploadAndSaveProductImage(avatarFile,newCustomer,newAvatar);
            }
        } else {
            String cloudId = "avatar_images/" + avatarDTO.getId();
            avatarRepository.deleteById(avatarDTO.getId());

            if (fileType.equals(FileType.IMAGE.getValue())) {
                deleteAndUploadImage(avatarFile, newCustomer,cloudId, newAvatar);
            }
        }


        CustomerAvartasDTO customerAvartasDTO = newCustomer.toCustomerAvartasDTO();
        customerAvartasDTO.setAvatarDTO(newAvatar.toAvatarDTO());

        return customerAvartasDTO;
    }

    private void uploadAndSaveProductImage(MultipartFile avatarFile, Customer customer, Avatar avatar) {
        try {
            Map uploadResult = uploadService.uploadImage(avatarFile, uploadUtils.buildImageUploadParams(avatar));
            String fileUrl = (String) uploadResult.get("secure_url");
            String fileFormat = (String) uploadResult.get("format");

            avatar.setFileName(avatar.getId() + "." + fileFormat);
            avatar.setFileUrl(fileUrl);
            avatar.setFileFolder(UploadUtils.IMAGE_UPLOAD_FOLDER);
            avatar.setCloudId(avatar.getFileFolder() + "/" + avatar.getId());
            avatar.setCustomer(customer);
            avatarRepository.save(avatar);

        } catch (IOException e) {
            e.printStackTrace();
            throw new DataInputException("Upload hình ảnh thất bại");
        }
    }

    private void deleteAndUploadImage(MultipartFile avatarFile,Customer customer, String cloudId, Avatar avatar) {
        try {
            Map deleteResult = uploadService.destroyImage(cloudId, uploadUtils.buildImageDestroyParams(customer,cloudId));

            Map uploadResult = uploadService.uploadImage(avatarFile, uploadUtils.buildImageUploadParams(avatar));
            String fileUrl = (String) uploadResult.get("secure_url");
            String fileFormat = (String) uploadResult.get("format");

            avatar.setFileName(avatar.getId() + "." + fileFormat);
            avatar.setFileUrl(fileUrl);
            avatar.setFileFolder(UploadUtils.IMAGE_UPLOAD_FOLDER);
            avatar.setCloudId(avatar.getFileFolder() + "/" + avatar.getId());
            avatar.setCustomer(customer);
            avatarRepository.save(avatar);

        } catch (IOException e) {
            e.printStackTrace();
            throw new DataInputException("Upload hình ảnh thất bại");
        }
    }

    @Override
    public CustomerAvartasDTO getCustomersAvartaDTOById(Long id) {
        return customerRepository.getCustomersAvartaDTOById(id);
    }
}
