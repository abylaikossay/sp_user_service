package kz.smart.plaza.users.models.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificateCodeRequest {
    private String code;
    private String zipCode;
    private String barcodeUrl;
}
