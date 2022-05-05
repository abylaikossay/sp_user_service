package kz.smart.plaza.users.models.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kz.smart.plaza.users.models.entities.audits.AuditModel;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user_promos")
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_user_promos",
        initialValue = 1,
        allocationSize = 1)
@ApiModel(description = "User Promo table")
public class UserPromo extends AuditModel {
    @ApiModelProperty(notes = "Id of promo action")
    @Column(name = "promo_id")
    private Long promoId;

    @ApiModelProperty(notes = "Data of user table")
    @ManyToOne
    private User user;

    @ApiModelProperty(notes = "Id of Brand to reward")
    @Column(name = "brand_id")
    private Long brandIdReward;

    @ApiModelProperty(notes = "Активировал пользователь акцию или нет")
    @Column(name = "active")
    private Boolean active;

    @ApiModelProperty(notes = "Name of reward")
    @Column(name = "reward_name")
    private String rewardName;

    @ApiModelProperty(notes = "Id of reward")
    @Column(name = "reward_id")
    private Long rewardId;

    @ApiModelProperty(notes = "Сумма или скидка для награды")
    @Column(name = "reward_amount")
    private Long rewardAmount;

    @ApiModelProperty(notes = "Сам промокод")
    @Column(name = "promo_code")
    private String promoCode;

}
