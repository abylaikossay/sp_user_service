package kz.smart.plaza.users.repositories;

import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import kz.smart.plaza.users.models.entities.QUserTemplate;
import kz.smart.plaza.users.models.entities.UserTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.stereotype.Repository;


@Repository
public interface UserTemplateRepository extends JpaRepository<UserTemplate, Long>, QuerydslPredicateExecutor<UserTemplate>, QuerydslBinderCustomizer<QUserTemplate> {
    @Override
    default void customize(
            QuerydslBindings bindings, QUserTemplate root) {
        bindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
        bindings.excluding(root._super);
    }
    UserTemplate findFirstByPhoneOrderByCreatedAtDesc(String phone);

}
