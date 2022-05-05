package kz.smart.plaza.users.models.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kz.smart.plaza.users.models.entities.audits.AuditModel;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "certificate_codes")
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_certificate_codes",
        initialValue = 1,
        allocationSize = 1)
@ApiModel(description = "Certificates table")
public class CertificateCode extends AuditModel {
    @ApiModelProperty(notes = "Код сертификата")
    @Column(name = "code")
    private String code;

    @ApiModelProperty(notes = "Zip Код сертификата")
    @Column(name = "zip_code")
    private String zipCode;

    @ApiModelProperty(notes = "Certificate id")
    @ManyToOne
    private Certificate certificate;

    @ApiModelProperty(notes = "Barcode location")
    @Column(name = "barcode_url")
    private String barcodeUrl;

    @ApiModelProperty(notes = "Qr code location")
    @Column(name = "qr_url")
    private String qrUrl;

    @ApiModelProperty(notes = "Qr code string")
    @Column(name = "qrString")
    private String qrString;

    @ApiModelProperty(notes = "Active certificate or not")
    @Column(name = "is_activated")
    private Boolean isActivated = false;

    @ApiModelProperty(notes = "Active certificate or not")
    @Column(name = "from_brand")
    private Boolean fromBrand = false;

}
