package kz.smart.plaza.users.repositories;

import kz.smart.plaza.users.models.entities.UserClick;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserClickRepository extends JpaRepository<UserClick, Long> {
    Optional<UserClick> findFirstByUser_Id(Long id);


}
