package kz.smart.plaza.users.models.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kz.smart.plaza.users.models.entities.audits.AuditModel;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "user_attempts")
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_user_attempts",
        initialValue = 1,
        allocationSize = 1)
@ApiModel(description = "User Tags table")
public class UserAttempt extends AuditModel {

    @ApiModelProperty(notes = "Data of user table")
    @ManyToOne
    private User user;

    @ApiModelProperty(notes = "Количество попыток логина через смс")
    @Column(name = "sms_attempts")
    private Integer smsAttempts;

}
