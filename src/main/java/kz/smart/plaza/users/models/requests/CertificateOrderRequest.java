package kz.smart.plaza.users.models.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CertificateOrderRequest {
    @NotNull
    private Long userId;
    private Long brandId;
    private Double bonus = 0.0;
    private Double cash = 0.0;
    private Double creditCard = 0.0;
    private double promo = 0.0;
    private List<OrderProductRequest> orderProducts;
}
