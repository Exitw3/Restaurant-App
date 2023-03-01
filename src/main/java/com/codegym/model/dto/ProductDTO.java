package com.codegym.model.dto;

import com.codegym.model.Category;
import com.codegym.model.ProductAvatar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductDTO {

    private Long id;

    private String title;
    private BigDecimal price;
    private Long quantity;
    private String description;

    private BigDecimal oldPrice;
    private Long discount;
    private Boolean newCheck;
    private Category category;
    private ProductAvatar avatar;


}
