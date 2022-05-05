package kz.smart.plaza.users.repositories;

import kz.smart.plaza.users.models.entities.UserAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface UserAttemptsRepository extends JpaRepository<UserAttempt, Long> {

    @Query(value = "SELECT * from user_attempts WHERE sms_attempts > 1",nativeQuery = true)
    List<UserAttempt> getAllUsers();

    UserAttempt findDistinctByUser_Id(Long userId);


}
