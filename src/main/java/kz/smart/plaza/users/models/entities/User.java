package kz.smart.plaza.users.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kz.smart.plaza.users.models.entities.audits.AuditModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "users")
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_users",
        initialValue = 101100,
        allocationSize = 1)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Users table")
public class User extends AuditModel {

    @ApiModelProperty(notes = "Name of user")
    @Column(name = "name")
    private String name;

    @ApiModelProperty(notes = "Surname of user")
    @Column(name = "surname")
    private String surname;

    @ApiModelProperty(notes = "Phone of user")
    @Column(name = "phone")
    private String phone;

    @ApiModelProperty(notes = "Password of user")
    @Column(name = "password")
    private String password;

    @ApiModelProperty(notes = "City of user")
    @Column(name = "city_id")
    private Integer cityId;

    @ApiModelProperty(notes = "Is pin correct or not")
    @Column(name = "pincode")
    private Boolean pincode = false;

    @ApiModelProperty(notes = "Banned or not")
    @Column(name = "banned")
    private Boolean banned = false;

    @ApiModelProperty(notes = "Birth Date")
    @Column(name = "birth_date")
    private Date birthDate;

    @ApiModelProperty(notes = "Platform")
    @Column(name = "platform")
    private String platform;

    @ApiModelProperty(notes = "email")
    @Column(name = "email")
    private String email;

    @ApiModelProperty(notes = "comment")
    @Column(columnDefinition="TEXT", name = "comment")
    private String comment;

    @ApiModelProperty(notes = "Last login")
    @Column(name = "last_login")
    private Date lastLogin;

    @ApiModelProperty(notes = "Device token of user")
    @Column(name = "device_token")
    private String deviceToken;

    @ApiModelProperty(notes = "Gender")
    @Column(name = "gender")
    private Integer gender;

    @ApiModelProperty(notes = "Language")
    @Column(name = "language")
    private String language;

    @ApiModelProperty(notes = "Avatar url")
    @Column(name = "avatar_url")
    private String avatar;

    @ApiModelProperty(notes = "QR")
    @Column(name = "qr")
    private String qr;

    @ApiModelProperty(notes = "Путь к qr коду")
    @Column(name = "qr_path")
    private String qrPath;

    @ApiModelProperty(notes = "Login code")
    @Column(name = "login_code")
    private String loginCode;

    @ApiModelProperty(notes = "Activation code")
    @Column(name = "activation_code")
    private String activationCode;

    @ApiModelProperty(notes = "Ведутся технические работы")
    @Column(name = "tech_work")
    private Boolean techWork = false;

    @ApiModelProperty(notes = "Push status")
    @Column(name = "pushable", columnDefinition = "boolean default true")
    private Boolean pushable = true;

    @ApiModelProperty(notes = "Template password for users")
    @Column(name = "password_template")
    private String passwordTemplate;

    @ApiModelProperty(notes = "Confirmed user")
    @Column(name = "is_confirmed", columnDefinition = "boolean default true")
    private Boolean isConfirmed = true;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "qr_updated_at", nullable = true)
    @LastModifiedDate
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date qrUpdatedAt = new Date();

    @ApiModelProperty(notes = "Version of mobile")
    @Column(name = "mobile_version")
    private String mobileVersion;

//    @ManyToMany
//    @JoinTable(name = "tag_user",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "tag_id"))
//    private List<Tag> tags;
}
