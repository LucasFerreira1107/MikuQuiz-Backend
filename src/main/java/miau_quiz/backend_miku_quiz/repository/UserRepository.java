package miau_quiz.backend_miku_quiz.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import miau_quiz.backend_miku_quiz.entity.User;
import miau_quiz.backend_miku_quiz.forms.UserPublic;

public interface UserRepository extends JpaRepository<User, UUID>{

	Optional<User> findByName(String name);
	
	User findByLogin(String login);

	@Query(value = "SELECT * FROM users WHERE email = ?1", nativeQuery = true)
	User findByEmail(String email);

	@Query(value = "SELECT * FROM users WHERE verification_code = ?1", nativeQuery = true)
	Optional<User> findByVerificationCode(String code);

	@Query(value = "SELECT u.name, SUM(att.score * " +
	           "    CASE q.difficulty " +
	           "        WHEN 'EASY' THEN 1.0 " +
	           "        WHEN 'MEDIUM' THEN 1.5 " +
	           "        WHEN 'HARD' THEN 2.0 " +
	           "        ELSE 1.0 " + // Um valor padrão caso a coluna seja nula ou diferente
	           "    END) AS rating " +
	           "FROM users u " +
	           "JOIN attempt att ON u.id = att.user_id " +
	           "JOIN quiz q ON att.quiz_id = q.quiz_id " +
	           "GROUP BY u.name " +
	           "ORDER BY rating DESC, u.name ASC", nativeQuery = true)
	List<UserPublic> findLeaderBoard();
	
	@Query(value = "SELECT u.name, SUM(att.score * " +
	           "    CASE q.difficulty " +
	           "        WHEN 'EASY' THEN 1.0 " +
	           "        WHEN 'MEDIUM' THEN 1.5 " +
	           "        WHEN 'HARD' THEN 2.0 " +
	           "        ELSE 1.0 " +
	           "    END) AS rating " +
	           "FROM users u " +
	           "JOIN attempt att ON u.id = att.user_id " +
	           "JOIN quiz q ON att.quiz_id = q.quiz_id " +
	           "WHERE u.id = ?1 " +
	           "GROUP BY u.name", nativeQuery = true)
	UserPublic findRatingByUserId(Long userId);

}
