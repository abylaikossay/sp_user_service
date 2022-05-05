package kz.smart.plaza.users.models.responses.notifications;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificateCertificateResponse {
    private Long externalId;
    private Long userId;
    private Long brandId;
    private String brandName;
    private String brandUrl;
    private Double sum;
}
