package kz.smart.plaza.users.models.responses.certificate;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCertificateResponse {
    private Double sum;
    private Date fromDate;
    private Date toDate;
    private String backgroundImg;
    private String qrBhImg;
    private Integer priceLocation;
    private Integer logoLocation;
    private Long brandId;
    private Long certificateCodeId;
    private String brandName;
    private String brandLogo;
    private Integer status;
    private Date buyTime;
}
