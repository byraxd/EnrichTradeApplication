package com.example.app.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Trade implements Serializable {

    private String date;
    private Integer productId;
    private String productName;
    private String currency;
    private String price;
}
