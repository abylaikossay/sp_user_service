package kz.smart.plaza.users.models.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kz.smart.plaza.users.models.entities.audits.AuditModel;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "covid_user")
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_covid_user",
        initialValue = 1,
        allocationSize = 1)
@ApiModel(description = "Covid Users table")
public class CovidUser extends AuditModel {
    @ApiModelProperty(notes = "Name of user")
    @Column(name = "name")
    private String name;

    @ApiModelProperty(notes = "Phone number of user")
    @Column(name = "phone")
    private String phone;

    @ApiModelProperty(notes = "Age of user")
    @Column(name = "age")
    private Integer age;

    @ApiModelProperty(notes = "Age of user")
    @Column(name = "user_photo")
    private String userPhoto;


}
