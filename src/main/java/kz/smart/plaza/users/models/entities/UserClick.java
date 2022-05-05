package kz.smart.plaza.users.models.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kz.smart.plaza.users.models.entities.audits.AuditModel;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user_clicks")
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_user_clicks",
        initialValue = 1,
        allocationSize = 1)
@ApiModel(description = "User Clicks table")
public class UserClick extends AuditModel {

    @ApiModelProperty(notes = "Data of user table")
    @OneToOne
    private User user;

    @ApiModelProperty(notes = "Количество кликов на верхний баннер")
    @Column(name = "top_baner")
    private Integer topBanner;

    @ApiModelProperty(notes = "Количество кликов на нижний баннер")
    @Column(name = "bottom_baner")
    private Integer bottomBanner;

    @ApiModelProperty(notes = "Количество кликов на partners")
    @Column(name = "partners")
    private Integer partners;

    @ApiModelProperty(notes = "Количество кликов на my qr")
    @Column(name = "my_qr")
    private Integer myQr;

    @ApiModelProperty(notes = "Количество кликов на uploadCheque")
    @Column(name = "upload_cheque")
    private Integer uploadCheque;

    @ApiModelProperty(notes = "Количество кликов на onlineMarket")
    @Column(name = "online_market")
    private Integer onlineMarket;

    @ApiModelProperty(notes = "Количество кликов на cinemax")
    @Column(name = "cinemax")
    private Integer cinemax;

    @ApiModelProperty(notes = "Количество кликов на parking")
    @Column(name = "parking")
    private Integer parking;

    @ApiModelProperty(notes = "Количество кликов на trcMap")
    @Column(name = "trc_map")
    private Integer trcMap;

    @ApiModelProperty(notes = "Количество кликов на news")
    @Column(name = "news")
    private Integer news;

    @ApiModelProperty(notes = "Количество кликов на promo")
    @Column(name = "promo")
    private Integer promo;

    @ApiModelProperty(notes = "Количество кликов на startTime")
    @Column(name = "start_time")
    private Integer startTime;

    @ApiModelProperty(notes = "Количество кликов на eco")
    @Column(name = "eco")
    private Integer eco;
}
