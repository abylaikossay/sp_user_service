package kz.smart.plaza.users.repositories;

import kz.smart.plaza.users.models.entities.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    List<Certificate> findAllByDeletedAtIsNullAndBrandIdOrderBySumAsc(Long brandId);

    Optional<Certificate> findFirstByDeletedAtIsNullAndBrandIdAndSum(Long brandId, Double sum);

    Certificate findFirstByIdAndDeletedAtIsNull(Long id);

//    UserTag findFirstByIdAndBrand_Id(Long id, Long brandId);
//List<UserPromo> findAllByDeletedAtIsNullAndUser_Id(Long userId);

}
