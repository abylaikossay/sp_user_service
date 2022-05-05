package kz.smart.plaza.users.models.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kz.smart.plaza.users.models.entities.audits.AuditModel;
import kz.smart.plaza.users.models.responses.jsonSerializer.JsonDateDeserializer;
import kz.smart.plaza.users.models.responses.jsonSerializer.JsonDateSerializer;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "certificates")
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_certificates",
        initialValue = 1,
        allocationSize = 1)
@ApiModel(description = "Certificates table")
public class Certificate extends AuditModel {
    //Grid location
    public static Integer LEFTTOP = 1;
    public static Integer RIGHTTOP = 2;
    public static Integer LEFTBOTTOM = 3;
    public static Integer RIGHTBOTTOM = 3;

    @ApiModelProperty(notes = "Номинал сертификата")
    @Column(name = "sum")
    private Double sum;

    @ApiModelProperty(notes = "Дата начала активации сертификата")
    @Column(name = "from_date")
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date fromDate;

    @ApiModelProperty(notes = "Дата окончания сертификата")
    @Column(name = "to_date")
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date toDate;

    @ApiModelProperty(notes = "Картинка на фоне сертификата")
    @Column(name = "background_img")
    private String backgroundImg;

    @ApiModelProperty(notes = "Картинка на фоне c qr-ом")
    @Column(name = "qr_bg_img")
    private String qrBgImg;

    @ApiModelProperty(notes = "Расположение цены")
    @Column(name = "price_location")
    private Integer priceLocation;

    @ApiModelProperty(notes = "Расположение лого")
    @Column(name = "logo_location")
    private Integer logoLocation;

    @ApiModelProperty(notes = "Айди бренда сертификата")
    @Column(name = "brand_id")
    private Long brandId;

    @ApiModelProperty(notes = "Ограничено количество сертификатов или нет")
    @Column(name = "has_limit")
    private Boolean hasLimit;

    @ApiModelProperty(notes = "Месяц истечения срока действия")
    @Column(columnDefinition = "integer default 6", name = "expirationMonth")
    private Integer expirationMonth = 6;

}
