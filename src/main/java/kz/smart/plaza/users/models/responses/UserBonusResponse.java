package kz.smart.plaza.users.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserBonusResponse {
    private Double bonus;
    private Double activeBonus;
    private Double blockedBonus;
    private Double ecoBonus;
    private Double activeEcoBonus;
    private Long userId;

}
