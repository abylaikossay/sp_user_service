package kz.smart.plaza.users.models.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kz.smart.plaza.users.models.entities.audits.AuditModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "texts")
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_texts",
        initialValue = 1,
        allocationSize = 1)
@ApiModel(description = "Texts table")
public class Text extends AuditModel {
    @ApiModelProperty(notes = "Name of tag")
    @Column(columnDefinition="TEXT", name = "message")
    private String message;

    @ApiModelProperty(notes = "Can we block transactions or not")
    @Column(name = "status")
    private Boolean status;

}
