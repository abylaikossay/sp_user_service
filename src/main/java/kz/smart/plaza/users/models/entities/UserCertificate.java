package kz.smart.plaza.users.models.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kz.smart.plaza.users.models.entities.audits.AuditModel;
import kz.smart.plaza.users.models.responses.jsonSerializer.JsonDateDeserializer;
import kz.smart.plaza.users.models.responses.jsonSerializer.JsonDateSerializer;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "user_certificates")
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_user_certificates",
        initialValue = 1,
        allocationSize = 1)
@ApiModel(description = "User Certificates table")
public class UserCertificate extends AuditModel {
    //Status
    public static Integer ACTIVE = 1;
    public static Integer USED = 0;
    public static Integer EXPIRED = 2;
    public static Integer GIFTED = 3;

    @ApiModelProperty(notes = "Data of user table")
    @ManyToOne
    private User user;

    @ApiModelProperty(notes = "Сертификад с кодом для пользователя")
    @ManyToOne
    private CertificateCode certificateCode;

    @ApiModelProperty(notes = "Статус сертификата")
    @Column(name = "status")
    private Integer status;

    @ApiModelProperty(notes = "От кого был подарен сертификат")
    @Column(name = "gifted_user_id")
    private Long giftedUserId;

    @ApiModelProperty(notes = "Дата окончания сертификата")
    @Column(name = "to_date")
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date toDate;
}
