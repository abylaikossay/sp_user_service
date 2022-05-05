package kz.smart.plaza.users.repositories;

import kz.smart.plaza.users.models.entities.CertificateCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateCodeRepository extends JpaRepository<CertificateCode, Long> {

    List<CertificateCode> findAllByCertificate_IdAndIsActivatedIsFalse(Long id);


    List<CertificateCode> findAllByCertificate_IdAndFromBrandIsTrue(Long id);

    Optional<CertificateCode> findFirstByQrString(String qr);

    CertificateCode findFirstByCode(String code);



//    UserTag findFirstByIdAndBrand_Id(Long id, Long brandId);
//List<UserPromo> findAllByDeletedAtIsNullAndUser_Id(Long userId);

}
