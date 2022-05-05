package kz.smart.plaza.users.models.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kz.smart.plaza.users.models.entities.audits.AuditModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "button_infos")
@Builder
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_button_infos",
        initialValue = 1,
        allocationSize = 1)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Main Page button infos table")
public class ButtonInfo extends AuditModel {

    public static final Integer IOS = 1;
    public static final Integer ANDROID = 0;

    @ApiModelProperty(notes = "Id кнопки")
    @Column(name = "button_id")
    private Long buttonId;

    @ApiModelProperty(notes = "Наименование кнопки")
    @Column(name = "button_name")
    private String buttonName;

    @ApiModelProperty(notes = "Фото кнопки")
    @Column(name = "button_img")
    private String buttonImg;

    @ApiModelProperty(notes = "Платформа")
    @Column(name = "platform")
    private Integer platform;

    @ApiModelProperty(notes = "Статус кнопки")
    @Column(name = "active")
    private Boolean active;

    @ApiModelProperty(notes = "Язык")
    @Column(name = "language")
    private String language;


}
