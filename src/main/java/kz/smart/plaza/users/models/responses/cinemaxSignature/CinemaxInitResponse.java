package kz.smart.plaza.users.models.responses.cinemaxSignature;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CinemaxInitResponse {
    private Long user_id;
    private Boolean authorised;
    private String phone;
    private String qr;
    private Integer modifier;
}
