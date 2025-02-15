package com.example.app.csvRepresentation;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeRepresentation {
    @CsvBindByName(column = "date")
    private String date;

    @CsvBindByName(column = "productId")
    private Integer productId;

    @CsvBindByName(column = "currency")
    private String currency;

    @CsvBindByName(column = "price")
    private String price;
}