package miau_quiz.backend_miku_quiz.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import miau_quiz.backend_miku_quiz.dto.RankingEntryDTO;
import miau_quiz.backend_miku_quiz.entity.Attempt;

public interface AttemptRepository extends JpaRepository<Attempt, UUID> {
	@Query(value = "SELECT ROUND(AVG(rating), 2) FROM attempt WHERE quiz_id = ?1", nativeQuery = true)
	Double findQuizRating(UUID quizId);
	
	@Query(value = "SELECT COUNT(*) FROM attempt WHERE user_id = ?1", nativeQuery = true)
	Integer findAttemptsByUserId(UUID userId);
	
	@Query(value = "SELECT COUNT(*) FROM attempt WHERE user_id = ?1 AND quiz_id = ?2", nativeQuery = true)
	Integer findAttemptsForTheQuizByUserId(UUID uuid, UUID uuid2);

	/**
     * RANKING GLOBAL
     * Calcula a soma total de pontos (score) para cada utilizador,
     * agrupa por utilizador e ordena do maior para o menor.
     * Usa um construtor de DTO para retornar os dados já formatados.
     */
	@Query("SELECT new miau_quiz.backend_miku_quiz.dto.RankingEntryDTO(" +
	           "   a.user.userId, a.user.name, a.user.avatarUrl, " +
	           "   SUM(a.score), " +
	           "   AVG(a.accuracy) * 100, " +
	           "   MAX(a.maxStreak)" +
	           ") " +
	           "FROM Attempt a " +
	           "GROUP BY a.user.userId, a.user.name, a.user.avatarUrl " +
	           "ORDER BY SUM(a.score) DESC")
	    List<RankingEntryDTO> findGlobalRanking();
	
	/**
    * RANKING POR QUIZ
    * Para um quiz específico, busca a MAIOR pontuação de cada utilizador,
    * agrupa por utilizador e ordena da maior para a menor.
    */
	@Query("SELECT new miau_quiz.backend_miku_quiz.dto.RankingEntryDTO(" +
	           "   a.user.userId, a.user.name, a.user.avatarUrl, " +
	           "   MAX(a.score), " +
	           "   AVG(a.accuracy), " +
	           "   MAX(a.maxStreak)" +
	           ") " +
	           "FROM Attempt a " +
	           "WHERE a.quiz.id = :quizId " +
	           "GROUP BY a.user.userId, a.user.name, a.user.avatarUrl " +
	           "ORDER BY MAX(a.score) DESC")
	    List<RankingEntryDTO> findRankingForQuiz(@Param("quizId") UUID quizId);	
}
