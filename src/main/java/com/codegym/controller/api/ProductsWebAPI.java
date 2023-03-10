package com.codegym.controller.api;

import com.codegym.exception.DataInputException;
import com.codegym.model.Category;
import com.codegym.model.Product;
import com.codegym.model.dto.CategoryDTO;
import com.codegym.model.dto.ProductDTO;
import com.codegym.service.category.ICategoryService;
import com.codegym.service.product.IProductService;
import com.codegym.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/web/api/products")
public class ProductsWebAPI {

    @Autowired
    private IProductService productService;

    @Autowired
    private AppUtils appUtils;

    @Autowired
    private ICategoryService categoryService;


    @GetMapping
    public ResponseEntity<?> getAll() {

        List<ProductDTO> productDTOS = productService.getAllProductDTO();

        return new ResponseEntity<>(productDTOS, HttpStatus.OK);
    }

    @GetMapping("/top5")
    public ResponseEntity<?> getTop5() {

        List<ProductDTO> productDTOS = productService.getNewProductDTO(PageRequest.of(0,5));

        return new ResponseEntity<>(productDTOS, HttpStatus.OK);
    }


    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {

        List<Category> categories = categoryService.findAll();

        List<CategoryDTO> categoryDTOS = new ArrayList<>();
        for (Category item : categories) {
            CategoryDTO categoryDTO = item.toCategoryDTO();
            categoryDTOS.add(categoryDTO);
        }

        return new ResponseEntity<>(categoryDTOS, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable Long productId) {

        Optional<Product> productOptional = productService.findById(productId);

        if (!productOptional.isPresent()) {
            throw new DataInputException("ID s???n ph???m kh??ng h???p l???");
        }
        return new ResponseEntity<>(productOptional.get().toProductDTO(), HttpStatus.OK);
    }

}
