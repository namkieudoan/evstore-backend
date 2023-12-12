package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {
    @Min(value = 1, message = "order must be >= 0")
    @JsonProperty("order_id")
    private Long orderID;

    @JsonProperty("product_id")
    private Long productId;

    @Min(value = 1, message = "order's price must be >= 0")
    private Float price;

    @JsonProperty("number_of_products")
    private int numberOfProducts;

    @Min(value = 1, message = "total_money must be >= 0")
    @JsonProperty("total_money")
    private Float totalMoney;

    private String color;
}
