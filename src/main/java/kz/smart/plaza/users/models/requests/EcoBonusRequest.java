package kz.smart.plaza.users.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EcoBonusRequest {
    private Long id;
    private Double ecoBonuses;
    private Double activeEcoBonuses;
    private Long userId;
}
