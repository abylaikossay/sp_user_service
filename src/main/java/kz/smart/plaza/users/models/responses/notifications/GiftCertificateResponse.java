package kz.smart.plaza.users.models.responses.notifications;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GiftCertificateResponse {
    private Long externalId;
    private Double sum;
    private Long userId;
    private String userName;
    private String userPhone;
    private Long brandId;
    private String brandName;
    private String brandUrl;
}
