package kz.smart.plaza.users.models.responses.webCdm;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class CinemaxUserRegisteredResponse {
    private Long userId;
    private String phone;
}
