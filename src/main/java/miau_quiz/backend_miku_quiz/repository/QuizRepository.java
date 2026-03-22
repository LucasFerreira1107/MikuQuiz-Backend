package miau_quiz.backend_miku_quiz.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import miau_quiz.backend_miku_quiz.Enums.QuizStatus;
import miau_quiz.backend_miku_quiz.entity.Quiz;

public interface QuizRepository extends JpaRepository<Quiz, UUID> , JpaSpecificationExecutor<Quiz>{
	@Query(value = "SELECT * FROM quiz WHERE user_id <> ?1 AND status = 'Published'", nativeQuery = true)
	List<Quiz> findPublishedQuizzesFromOtherUsers(UUID uuid);

	@Query(value = "SELECT * FROM quiz WHERE status = 'Published'", nativeQuery = true)
	List<Quiz> findAllPublished();

	@Query(value = "SELECT * FROM quiz WHERE user_id = ?1", nativeQuery = true)
	List<Quiz> findQuizzesByUserId(UUID uuid);

	@Query("SELECT DISTINCT q FROM Quiz q LEFT JOIN FETCH q.tags LEFT JOIN FETCH q.questions")
	List<Quiz> findAllWithDetails();

	
	@Query("SELECT DISTINCT q FROM Quiz q LEFT JOIN FETCH q.tags")
	List<Quiz> findAllWithTags();

	// 
	@Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.tags LEFT JOIN FETCH q.questions WHERE q.id = :id")
	Optional<Quiz> findByIdWithDetails(UUID id);

	@Query("SELECT DISTINCT q FROM Quiz q " + "LEFT JOIN FETCH q.tags " + "LEFT JOIN FETCH q.questions "
			+ "WHERE q.status = :status")
	List<Quiz> findAllWithDetailsByStatus(@Param("status") QuizStatus status);
}
