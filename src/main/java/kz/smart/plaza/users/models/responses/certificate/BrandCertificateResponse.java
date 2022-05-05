package kz.smart.plaza.users.models.responses.certificate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandCertificateResponse {
    private String code;
    private String zipCode;
    private Double sum;
    private Long brandId;
    private Long certificateCodeId;
    private String brandName;
    private String brandLogo;
    private Boolean isActivated;
}
