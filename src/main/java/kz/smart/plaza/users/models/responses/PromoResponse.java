package kz.smart.plaza.users.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromoResponse {
    private Long userId;
    private String userName;
    private Long brandId;
    private String promoCode;
    private Long rewardAmount;
    private String rewardName;

}
