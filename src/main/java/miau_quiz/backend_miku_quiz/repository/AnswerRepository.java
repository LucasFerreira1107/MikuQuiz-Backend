package miau_quiz.backend_miku_quiz.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import miau_quiz.backend_miku_quiz.entity.Answer;

public interface AnswerRepository extends JpaRepository<Answer, UUID>{

	@Query(value="SELECT * FROM answer WHERE question_id = ?1", nativeQuery=true)
	List<Answer> findByQuestionId(Long questionId);
	
	@Query(value="SELECT * FROM answer WHERE is_correct AND question_id = ?1", nativeQuery = true)
	Answer findCorrectByQuestionId(Long questionId);
	
	@Query(value="SELECT text FROM answer WHERE question_id = ?1", nativeQuery=true)
	List<String> findAnswerTextsByQuestionId(Long questionId);

}
