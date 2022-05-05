package kz.smart.plaza.users.models.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class OrderProductRequest {
    private double amount = 1.0;
    private Double price;
    private String productName;
    private Boolean returnable = true;
    @NotNull
    private Long categoryId;
}
