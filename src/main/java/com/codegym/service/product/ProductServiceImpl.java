package com.codegym.service.product;

import com.codegym.exception.DataInputException;
import com.codegym.model.Category;
import com.codegym.model.Product;
import com.codegym.model.ProductAvatar;
import com.codegym.model.dto.ProductCreateDTO;
import com.codegym.model.dto.ProductDTO;
import com.codegym.model.enums.FileType;
import com.codegym.repository.ProductAvatarRepository;
import com.codegym.repository.ProductRepository;
import com.codegym.service.category.ICategoryService;
import com.codegym.service.upload.UploadService;
import com.codegym.utils.UploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductAvatarRepository productAvatarRepository;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private UploadUtils uploadUtils;


    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }


    @Override
    public List<ProductDTO> getAllProductDTO() {
        return productRepository.getAllProductDTO();
    }

    @Override
    public List<Product> findAllByDeletedIsFalse() {
        return productRepository.findAllByDeletedIsFalse();
    }

    @Override
    public List<ProductDTO> getNewProductDTO(Pageable pageable) {
        return productRepository.getNewProductDTO(pageable);
    }

    @Override
    public Product getById(Long id) {
        return productRepository.getById(id);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product create(ProductCreateDTO productCreateDTO) throws IOException {

        ProductAvatar productAvatar = new ProductAvatar();
        productAvatar = productAvatarRepository.save(productAvatar);

        ProductAvatar newProductAvatar = uploadAndSaveProductAvatar(productCreateDTO, productAvatar);

        Category category = categoryService.findById(Long.valueOf(productCreateDTO.getCategoryId())).get();
        Product newProduct = productCreateDTO.toProduct(newProductAvatar);
        newProduct.setCategory(category);
        if (productCreateDTO.getDiscount() != null) {
            newProduct.setDiscount(Long.valueOf(productCreateDTO.getDiscount()));
        }
        if (productCreateDTO.getNewCheck() != null) {
            newProduct.setNewCheck(productCreateDTO.getNewCheck());
        }
        if (productCreateDTO.getOldPrice() != null) {
            newProduct.setOldPrice(BigDecimal.valueOf(Long.parseLong(productCreateDTO.getOldPrice())));
        }


        Product product = new Product();
        try {

            product = productRepository.save(newProduct);
        } catch (Exception e) {
            deleteProductAvatar(productAvatar.getCloudId());
        }

        return product;
    }

    @Override
    public Product update(Product product, ProductCreateDTO productCreateDTO) {

        productAvatarRepository.deleteById(product.getProductAvatar().getId());
        String cloudId = "product_images/" + product.getProductAvatar().getId();

        ProductAvatar productAvatar = new ProductAvatar();
        productAvatar = productAvatarRepository.save(productAvatar);

        ProductAvatar newProductAvatar = deleteAndUploadProductAvatar(productCreateDTO,product,cloudId, productAvatar);

        product.setProductAvatar(newProductAvatar);

        Product newProduct = productRepository.save(product);

        return newProduct;
    }


    private ProductAvatar uploadAndSaveProductAvatar(ProductCreateDTO productCreateDTO, ProductAvatar productAvatar) {
        try {
            Map uploadResult = uploadService.uploadImage(productCreateDTO.getFile(), uploadUtils.buildImageUploadParams(productAvatar));
            String fileUrl = (String) uploadResult.get("secure_url");
            String fileFormat = (String) uploadResult.get("format");

            productAvatar.setFileName(productAvatar.getId() + "." + fileFormat);
            productAvatar.setFileType(FileType.IMAGE.getValue());
            productAvatar.setFileUrl(fileUrl);
            productAvatar.setFileFolder(UploadUtils.PRODUCT_IMAGE_UPLOAD_FOLDER);
            productAvatar.setCloudId(productAvatar.getFileFolder() + "/" + productAvatar.getId());
            productAvatar = productAvatarRepository.save(productAvatar);
            return productAvatar;

        } catch (IOException e) {
            e.printStackTrace();
            throw new DataInputException("Upload h??nh ???nh th???t b???i");
        }
    }

    private void deleteProductAvatar(String cloudId) throws IOException {
        uploadService.destroyImage(cloudId, uploadUtils.buildImageDestroyParams(cloudId));
    }

    private ProductAvatar deleteAndUploadProductAvatar(ProductCreateDTO productCreateDTO,Product product, String cloudId, ProductAvatar productAvatar) {
        try {
            uploadService.destroyImage(cloudId, uploadUtils.buildImageDestroyParams(product,cloudId));

            Map uploadResult = uploadService.uploadImage(productCreateDTO.getFile(), uploadUtils.buildImageUploadParams(productAvatar));
            String fileUrl = (String) uploadResult.get("secure_url");
            String fileFormat = (String) uploadResult.get("format");

            productAvatar.setFileName(productAvatar.getId() + "." + fileFormat);
            productAvatar.setFileType(FileType.IMAGE.getValue());
            productAvatar.setFileUrl(fileUrl);
            productAvatar.setFileFolder(UploadUtils.PRODUCT_IMAGE_UPLOAD_FOLDER);
            productAvatar.setCloudId(productAvatar.getFileFolder() + "/" + productAvatar.getId());
            productAvatar = productAvatarRepository.save(productAvatar);
            return productAvatar;

        } catch (IOException e) {
            e.printStackTrace();
            throw new DataInputException("Upload h??nh ???nh th???t b???i");
        }
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void remove(Long id) {

    }
}
