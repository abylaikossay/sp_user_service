package kz.smart.plaza.users.repositories;

import kz.smart.plaza.users.models.entities.UserTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTagRepository extends JpaRepository<UserTag, Long> {
//    UserTag findFirstByIdAndBrand_Id(Long id, Long brandId);
List<UserTag> findAllByDeletedAtIsNullAndUser_Id(Long userId);
List<UserTag> findAllByDeletedAtIsNullAndIdNotInAndUser_Id(List<Long> ids, Long userId);
UserTag findFirstByDeletedAtIsNullAndId(Long id);

}
