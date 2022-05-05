package kz.smart.plaza.users.models.responses.certificate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import kz.smart.plaza.users.models.responses.UserFeignResponse;
import kz.smart.plaza.users.models.responses.jsonSerializer.JsonDateDeserializer;
import kz.smart.plaza.users.models.responses.jsonSerializer.JsonDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificateCodeResponse {
    private Long id;
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date createdAt;
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date updatedAt;
    private String code;
    private String zipCode;
    private CertificateResponse certificate;
    private String barcodeUrl;
    private String qrUrl;
    private String qrString;
    private Integer status;
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date buyTime;
    private Boolean isActivated;
    private Boolean fromBrand;
    private UserFeignResponse user;
}
