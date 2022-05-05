package kz.smart.plaza.users.repositories;

import kz.smart.plaza.users.models.entities.CovidUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CovidUserRepository extends JpaRepository<CovidUser, Long> {

//    UserTag findFirstByIdAndBrand_Id(Long id, Long brandId);
//List<UserPromo> findAllByDeletedAtIsNullAndUser_Id(Long userId);

}
