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
@Table(name = "feedbacks")
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_feedbacks",
        initialValue = 1,
        allocationSize = 1)
@ApiModel(description = "Feedbacks table")
public class Feedback extends AuditModel {
    @ApiModelProperty(notes = "Question of user")
    @Column(columnDefinition="TEXT", name = "question")
    private String question;

    @ApiModelProperty(notes = "User Phone")
    @Column(name = "phone")
    private String phone;

    @ApiModelProperty(notes = "Решен вопрос или нет")
    @Column(name = "is_solved")
    private Boolean isSolved;

}
