package kz.smart.plaza.users.controllers.rest.v1;

import io.swagger.annotations.ApiOperation;
import kz.smart.plaza.users.controllers.BaseController;
import kz.smart.plaza.users.methods.BarbecueBarcodeGenerator;
import kz.smart.plaza.users.models.errors.ServiceException;
import kz.smart.plaza.users.models.requests.BuyCertificateRequest;
import kz.smart.plaza.users.models.requests.CertificateCodeRequest;
import kz.smart.plaza.users.models.requests.CertificateRequest;
import kz.smart.plaza.users.services.v1.CertificateServiceV1;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.util.List;


@RestController
@RequestMapping("/api/v1/certificate")
@AllArgsConstructor
public class CertificateControllerV1 extends BaseController {

    private CertificateServiceV1 certificateServiceV1;
    private BarbecueBarcodeGenerator barbecueBarcodeGenerator;

    @PostMapping(value = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation("Добавить сертификат")
    public ResponseEntity<?> addNewCertificate(@RequestHeader(value = "userId") Long employeeId, @ModelAttribute CertificateRequest certificateRequest) throws ServiceException {
        return buildResponse(certificateServiceV1.addNewCertificate(certificateRequest, employeeId), HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation("Редактировать сертификат")
    public ResponseEntity<?> updateCertificate(@RequestHeader(value = "userId") Long employeeId,
                                               @ModelAttribute CertificateRequest certificateRequest, @PathVariable Long id) throws ServiceException {
        return buildResponse(certificateServiceV1.updateCertificate(certificateRequest, id, employeeId), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation("Удаление сертификата")
    public ResponseEntity<?> deleteCertificate(@RequestHeader(value = "userId") Long employeeId,
                                               @PathVariable Long id) throws ServiceException {
        return buildResponse(certificateServiceV1.deleteCertificate(employeeId, id), HttpStatus.OK);
    }


    @GetMapping("/brand")
    public ResponseEntity<?> getCertificatesByBrand(@RequestParam(value = "brandId") Long brandId) {
        return buildResponse(certificateServiceV1.getCertificatesByBrand(brandId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCertificateById(@PathVariable Long id) {
        return buildResponse(certificateServiceV1.getCertificateByCertificateId(id), HttpStatus.OK);
    }

    @PostMapping(value = "/add-code")
    @ApiOperation("Добавить код для сертификата")
    public ResponseEntity<?> addCodeToCertificate(@RequestHeader(value = "userId") Long employeeId,
                                                  @RequestBody List<CertificateCodeRequest> codes, @RequestParam Long certificateId) {
        certificateServiceV1.addCodeToCertificate(employeeId, codes, certificateId);
        return buildSuccess("Success", "Success");
    }

    @DeleteMapping(value = "/delete/code/{id}")
    @ApiOperation("Удалить код сертификата")
    public ResponseEntity<?> deleteCertificateCode(@PathVariable Long id, @RequestHeader(value = "userId") Long employeeId) {
        return buildResponse(certificateServiceV1.deleteCertificateCode(id, employeeId), HttpStatus.OK);
    }

    @GetMapping(value = "/certificate-code")
    @ApiOperation("Получение кодов для сертификата")
    public ResponseEntity<?> getCertificateCodes(@RequestHeader(value = "userId") Long employeeId,
                                                  @RequestParam Long certificateId) {
        return buildResponse(certificateServiceV1.getCertificateCodes(employeeId, certificateId), HttpStatus.OK);
    }


    @GetMapping("/user")
    public ResponseEntity<?> getUserCertificateCodes(@RequestHeader(value = "userId") Long userId, @RequestParam Integer status) {
        return buildResponse(certificateServiceV1.getUserCertificates(userId, status), HttpStatus.OK);
    }

    @GetMapping("/code/{id}")
    public ResponseEntity<?> getCertificateByCodeId(@PathVariable Long id, @RequestHeader(value = "userId") Long userId) {
        return buildResponse(certificateServiceV1.getCertificateById(id, userId), HttpStatus.OK);
    }


    @PostMapping("/buy")
    public ResponseEntity<?> buyCertificate(@RequestHeader Long userId, @RequestBody BuyCertificateRequest buyCertificateRequest) {
        return buildResponse(certificateServiceV1.buyCertificate(buyCertificateRequest, userId), HttpStatus.OK);
    }

    @GetMapping(value = "/ean13/{barcode}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> barbecueEAN13Barcode(@PathVariable("barcode") String barcode)
            throws Exception {
        System.out.println("Bar code: " + barcode);
        return new ResponseEntity<>(barbecueBarcodeGenerator.generateEAN13BarcodeImage4(barcode), HttpStatus.OK);
    }

    @GetMapping("/brand/employee")
    public ResponseEntity<?> getBrandCertificates(@RequestHeader(value = "userId") Long employeeId) {
        return buildResponse(certificateServiceV1.getCertificatesOfBrand(employeeId), HttpStatus.OK);
    }


    @PostMapping("/activate")
    public ResponseEntity<?> activateCertificateByQr(@RequestParam String qr,@RequestHeader(value = "userId") Long employeeId) {
        certificateServiceV1.activateCertificate(qr, employeeId);
        return buildSuccess("Success", "Success");

    }

    @PostMapping("/gift")
    public ResponseEntity<?> giftCertificate(@RequestHeader(value = "userId") Long userId, @RequestParam Long id, @RequestParam String phone) {
        certificateServiceV1.giftCertificate(userId, id, phone);
        return buildSuccess("Success", "Success");

    }

    @GetMapping("/qr")
    @ApiOperation("Получение сертификата по qr Коду")
    public ResponseEntity<?> getCertificateByQr(@RequestParam String qr) {
        return buildResponse(certificateServiceV1.getCertificateByCode(qr), HttpStatus.OK);
    }

}
