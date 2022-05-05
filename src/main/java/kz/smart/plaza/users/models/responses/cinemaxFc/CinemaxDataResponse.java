package kz.smart.plaza.users.models.responses.cinemaxFc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CinemaxDataResponse {
    private UserCinemaxFcResponse data;
}
