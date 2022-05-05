package kz.smart.plaza.users.services.v1;



import kz.smart.plaza.users.models.entities.Certificate;
import kz.smart.plaza.users.models.entities.CertificateCode;
import kz.smart.plaza.users.models.requests.BuyCertificateRequest;
import kz.smart.plaza.users.models.requests.CertificateCodeRequest;
import kz.smart.plaza.users.models.requests.CertificateRequest;
import kz.smart.plaza.users.models.responses.certificate.BrandCertificateResponse;
import kz.smart.plaza.users.models.responses.certificate.CertificateCodeResponse;
import kz.smart.plaza.users.models.responses.certificate.CertificateResponse;
import kz.smart.plaza.users.models.responses.certificate.UserCertificateResponse;
import kz.smart.plaza.users.models.responses.success.SuccessResponse;

import java.util.List;

public interface CertificateServiceV1 {
    SuccessResponse addNewCertificate(CertificateRequest certificateRequest, Long employeeId);

    SuccessResponse updateCertificate(CertificateRequest certificateRequest, Long id, Long employeeId);

    List<Certificate> getCertificatesByBrand(Long brandId);

    List<UserCertificateResponse> getUserCertificates(Long userId, Integer status);

    CertificateCodeResponse getCertificateById(Long id, Long userId);

    CertificateCodeResponse getCertificateByCode(String qr);

    SuccessResponse buyCertificate(BuyCertificateRequest buyCertificateRequest, Long userId);

    List<BrandCertificateResponse> getCertificatesOfBrand(Long employeeId);

    void activateCertificate(String qr, Long userId);

    void giftCertificate(Long userId, Long id, String phone);

    void addCodeToCertificate(Long userId, List<CertificateCodeRequest> codes, Long certificateId);

    CertificateResponse getCertificateByCertificateId(Long id);

    SuccessResponse deleteCertificate(Long employeeId, Long id);

    List<CertificateCode> getCertificateCodes(Long employeeId, Long certificateId);

    SuccessResponse deleteCertificateCode(Long id, Long employeeId);

    void checkUserCertificatesAndUpdateThemByExpiration();
}
