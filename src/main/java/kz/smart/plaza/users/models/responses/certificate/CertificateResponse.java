package kz.smart.plaza.users.models.responses.certificate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import kz.smart.plaza.users.models.responses.jsonSerializer.JsonDateDeserializer;
import kz.smart.plaza.users.models.responses.jsonSerializer.JsonDateSerializer;
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
public class CertificateResponse {
    private Long id;
    private Date updatedAt;
    private Date createdAt;
    private Double sum;
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date fromDate;
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date toDate;
    private String backgroundImg;
    private String qrBgImg;
    private Integer priceLocation;
    private Integer logoLocation;
    private Long brandId;
    private String brandName;
    private String brandLogo;
}
