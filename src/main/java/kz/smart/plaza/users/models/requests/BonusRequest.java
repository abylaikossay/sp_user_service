package kz.smart.plaza.users.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BonusRequest {
    private Long id;
    private Double bonuses;
    private Double activeBonuses;
    private Double blockedBonuses;
    private Long userId;
}
