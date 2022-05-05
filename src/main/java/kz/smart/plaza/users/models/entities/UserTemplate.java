package kz.smart.plaza.users.models.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kz.smart.plaza.users.models.entities.audits.AuditModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "users_template")
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_users_template",
        initialValue = 1,
        allocationSize = 1)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Users table")
public class UserTemplate extends AuditModel {

    @ApiModelProperty(notes = "Name of user")
    @Column(name = "name")
    private String name;

    @ApiModelProperty(notes = "Surname of user")
    @Column(name = "surname")
    private String surname;

    @ApiModelProperty(notes = "Phone of user")
    @Column(name = "phone")
    private String phone;

    @ApiModelProperty(notes = "City of user")
    @Column(name = "city_id")
    private Integer cityId;

    @ApiModelProperty(notes = "Password of user")
    @Column(name = "password")
    private String password;

    @ApiModelProperty(notes = "Language")
    @Column(name = "language")
    private String language;

    @ApiModelProperty(notes = "Birth Date")
    @Column(name = "birth_date")
    private Date birthDate;

    @ApiModelProperty(notes = "Platform")
    @Column(name = "platform")
    private String platform;

    @ApiModelProperty(notes = "email")
    @Column(name = "email")
    private String email;


    @ApiModelProperty(notes = "Gender")
    @Column(name = "gender")
    private Integer gender;


    @ApiModelProperty(notes = "Login code")
    @Column(name = "login_code")
    private String loginCode;

    @ApiModelProperty(notes = "Activation code")
    @Column(name = "activation_code")
    private String activationCode;
}
