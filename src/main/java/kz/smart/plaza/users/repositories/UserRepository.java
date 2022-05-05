package kz.smart.plaza.users.repositories;

import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import kz.smart.plaza.users.models.entities.Tag;
import kz.smart.plaza.users.models.entities.User;
import kz.smart.plaza.users.models.entities.QUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User>, QuerydslBinderCustomizer<QUser> {
    @Override
    default void customize(
            QuerydslBindings bindings, QUser root) {
        bindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
        bindings.excluding(root._super);
    }
    Optional<User> findByPhone(String phone);
    Optional<User> findByPhoneAndDeletedAtIsNull(String phone);
    Optional<User> findFirstByQrOrderByUpdatedAtDesc(String qr);
    List<User> findAllById_In(List<Long> ids);
    List<User> findAllByDeviceTokenAndDeletedAtIsNull(String deviceToken);
    Optional<User> findFirstById(Long userId);

    @Modifying
    @Query(value = "update User u set u.techWork = ?1")
    void updateTechWorkStatus(Boolean status);

    @Query(value = "SELECT COUNT (u) from users u",nativeQuery = true)
    int getCountOfUsers();


    @Query(value = "select id from users where last_login > '2020-06-16' and birth_date > '1970-01-01' and birth_date < '1995-01-01' limit 10000;", nativeQuery = true)
    List<Long> getUsersForKuka();

    @Query(value = "select * from users where id>101174 order by id asc;", nativeQuery = true)
    List<User> getChatUsers();

//    select count(*) from users where created_at > '2020-09-01';
    @Query(value = "SELECT COUNT (u) from users u WHERE u.created_at > ?1", nativeQuery = true)
    int getCountOfUsersInAMonth(Date createdAt);

    @Modifying
    @Query(value = "update User u set u.pushable = ?1 where u.id = ?2")
    void updatePushStatus(Boolean status, Long userId);

    @Query(value = "select * from users where qr is null and updated_at>'2021-01-01';", nativeQuery = true)
    List<User> getQrUsers();

//    List<User> findAllByTags(Tag tag);
}
