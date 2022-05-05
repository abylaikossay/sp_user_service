package kz.smart.plaza.users.models.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kz.smart.plaza.users.models.entities.audits.AuditModel;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "tags")
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_tags",
        initialValue = 1,
        allocationSize = 1)
@ApiModel(description = "Tags table")
public class Tag extends AuditModel {
    @ApiModelProperty(notes = "Name of tag")
    @Column(name = "name")
    private String name;

    @ApiModelProperty(notes = "Can we block transactions or not")
    @Column(name = "no_transaction_blocking")
    private Boolean noTransactionBlocking;

//    @ManyToMany
//    @JoinTable(name = "tag_user",
//            joinColumns = @JoinColumn(name = "tag_id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id"))
//    private List<User> users;
}
