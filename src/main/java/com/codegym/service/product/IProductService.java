package com.codegym.service.product;

import com.codegym.model.Product;
import com.codegym.model.dto.ProductCreateDTO;
import com.codegym.model.dto.ProductDTO;
import com.codegym.service.IGeneralService;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface IProductService extends IGeneralService<Product> {

    Product create(ProductCreateDTO productCreateDTO) throws IOException;
    Product update(Product product, ProductCreateDTO productCreateDTO);
    List<Product> findAllByDeletedIsFalse();
    List<ProductDTO> getAllProductDTO();
    List<ProductDTO> getNewProductDTO(Pageable pageable);
}
