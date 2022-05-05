package kz.smart.plaza.users.models.responses.cinemaxFc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCinemaxFcResponse {
    private String id;
    private String first_name;
    private String last_name;
    private String points;

}
