package miau_quiz.backend_miku_quiz.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import miau_quiz.backend_miku_quiz.entity.Tags;

public interface TagsRepository extends JpaRepository<Tags, UUID>{
	Optional<Tags> findByName(String name);
}
