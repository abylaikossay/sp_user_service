package kz.smart.plaza.users.models.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kz.smart.plaza.users.models.entities.audits.AuditModel;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "user_tags")
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_user_tags",
        initialValue = 1,
        allocationSize = 1)
@ApiModel(description = "User Tags table")
public class UserTag extends AuditModel {
    @ApiModelProperty(notes = "Data of tag table")
    @ManyToOne
    private Tag tag;

    @ApiModelProperty(notes = "Data of user table")
    @ManyToOne
    private User user;

}
