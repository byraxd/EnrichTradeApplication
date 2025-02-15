package com.example.app.entity;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Integer productId;
    private String productName;
}
