package com.example.app.csvRepresentation;

import lombok.*;

@Builder
public record ProductRepresentation(Integer productId, String productName) {
}
