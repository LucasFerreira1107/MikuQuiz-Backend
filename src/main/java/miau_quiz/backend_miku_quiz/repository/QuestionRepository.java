package miau_quiz.backend_miku_quiz.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import miau_quiz.backend_miku_quiz.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
	@Query(value="SELECT * FROM question WHERE quiz_id = ?1", nativeQuery=true)
	List<Question> findQuestionsByQuizId(UUID uuid);
}
