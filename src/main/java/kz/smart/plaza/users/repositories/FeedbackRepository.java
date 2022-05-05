package kz.smart.plaza.users.repositories;

import kz.smart.plaza.users.models.entities.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
//    List<Feedback> findAll();

    Page<Feedback> findAllByIsSolvedAndQuestionNot(Pageable var1, boolean isSolved, String question);


    Page<Feedback> findAllByQuestionNot(Pageable var1, String question);




}
