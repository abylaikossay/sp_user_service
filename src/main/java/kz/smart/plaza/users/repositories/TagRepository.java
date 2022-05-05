package kz.smart.plaza.users.repositories;

import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import kz.smart.plaza.users.models.entities.QTag;
import kz.smart.plaza.users.models.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TagRepository extends JpaRepository<Tag, Long>, QuerydslPredicateExecutor<Tag>, QuerydslBinderCustomizer<QTag> {
    @Override
    default void customize(
            QuerydslBindings bindings, QTag root) {
        bindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
        bindings.excluding(root._super);
    }
    List<Tag> findAllByDeletedAtIsNull();
    Tag findFirstById(Long tagId);
    List<Tag> findAllById_In(List<Long> tagIds);
    List<Tag> findAllByDeletedAtIsNullAndId_In(List<Long> tagIds);
}
