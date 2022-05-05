package kz.smart.plaza.users.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPromoRequest {
    private Long userId;
    private Long brandIdReward;
    private Long rewardId;
    private Long rewardAmount;
    private String rewardName;
    private String promoCode;
    private Long promoId;
    private Boolean active;
}
