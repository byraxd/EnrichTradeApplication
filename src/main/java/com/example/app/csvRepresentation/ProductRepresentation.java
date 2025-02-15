package com.example.app.csvRepresentation;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRepresentation {

    @CsvBindByName(column = "productId")
    private Integer productId;

    @CsvBindByName(column = "productName")
    private String productName;
}
