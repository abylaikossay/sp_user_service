package kz.smart.plaza.users.repositories;

import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import kz.smart.plaza.users.models.entities.QUserCertificate;
import kz.smart.plaza.users.models.entities.UserCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;

import java.util.List;
import java.util.Optional;

public interface UserCertificateRepository extends JpaRepository<UserCertificate, Long>, QuerydslPredicateExecutor<UserCertificate>, QuerydslBinderCustomizer<QUserCertificate> {

    @Override
    default void customize(
            QuerydslBindings bindings, QUserCertificate root) {
        bindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
        bindings.excluding(root._super);
    }

    List<UserCertificate> findAllByUser_IdAndStatusInOrderByIdDesc(Long id,List<Integer> status);

    Optional<UserCertificate> findFirstByCertificateCode_IdAndStatusIsNot(Long id, Integer status);

//    UserTag findFirstByIdAndBrand_Id(Long id, Long brandId);
//List<UserPromo> findAllByDeletedAtIsNullAndUser_Id(Long userId);

}
