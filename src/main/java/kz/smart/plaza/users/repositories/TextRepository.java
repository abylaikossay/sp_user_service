package kz.smart.plaza.users.repositories;

import kz.smart.plaza.users.models.entities.Text;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface TextRepository  extends JpaRepository<Text, Long> {

    Text findFirstByDeletedAtIsNull();
}
