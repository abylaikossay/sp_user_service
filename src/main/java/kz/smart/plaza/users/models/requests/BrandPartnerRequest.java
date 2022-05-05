package kz.smart.plaza.users.models.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandPartnerRequest {
    private Long brandId;
    private Long partnerId;
    private String brandName;
    private String partnerName;
    private String brandLogo;
    private Long cityId;
    private String cityName;
    private Double deliverySum;
    private Double deliveryFixedSum;
    private Double minOrderSum;
    private Integer userDeliveryCommission;
    private Integer userPickupCommission;
    private Integer brandDeliveryCommission;
    private Integer brandPickupCommission;
    private Boolean collectable;
}
