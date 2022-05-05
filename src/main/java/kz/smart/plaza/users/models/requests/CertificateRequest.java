package kz.smart.plaza.users.models.requests;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificateRequest {
    private Double sum;
    private Date fromDate;
    private Date toDate;
    private MultipartFile backgroundImg;
    private MultipartFile qrBgImg;
    private Integer priceLocation;
    private Integer logoLocation;
    private Long brandId;
    private Boolean hasLimit;


}
