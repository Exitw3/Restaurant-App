package com.codegym.service.customer;


import com.codegym.model.Customer;
import com.codegym.model.dto.AvatarDTO;
import com.codegym.model.dto.CustomerAvartasDTO;
import com.codegym.service.IGeneralService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ICustomerService extends IGeneralService<Customer> {

    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByEmailAndIdIsNot(String email, Long id);
    List<Customer> findAllByDeletedIsFalse();

    List<CustomerAvartasDTO> getAllCustomersAvartaDTO();
    CustomerAvartasDTO getCustomersAvartaDTOById(Long id);

    CustomerAvartasDTO saveWithAvatar(Customer customer, MultipartFile avatarFile);
    CustomerAvartasDTO updateWithAvatar(Customer customer, MultipartFile avatarFile, AvatarDTO avatarDTO);

}
