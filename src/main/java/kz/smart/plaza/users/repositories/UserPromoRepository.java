package kz.smart.plaza.users.repositories;

import kz.smart.plaza.users.models.entities.UserPromo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPromoRepository extends JpaRepository<UserPromo, Long> {
    List<UserPromo> findAllByUserId(Long ids);

//    UserTag findFirstByIdAndBrand_Id(Long id, Long brandId);
//List<UserPromo> findAllByDeletedAtIsNullAndUser_Id(Long userId);

}
