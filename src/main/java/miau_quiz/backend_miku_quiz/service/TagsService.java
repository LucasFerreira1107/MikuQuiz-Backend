package miau_quiz.backend_miku_quiz.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import miau_quiz.backend_miku_quiz.entity.Tags;
import miau_quiz.backend_miku_quiz.repository.TagsRepository;

@Service
@RequiredArgsConstructor
public class TagsService {

	private final TagsRepository tagsRepository;
	
	public List<Tags> getTags(){
		return tagsRepository.findAll();
	}
	
	public Set<Tags> findTagsFindByIds(Set<String> tagsId) {
		Set<UUID> uuidSet = tagsId.stream().map(UUID::fromString).collect(Collectors.toSet());

		List<Tags> foundTags = tagsRepository.findAllById(uuidSet);

		if (foundTags.size() != tagsId.size()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Uma ou mais Tags com os IDs fornecidos não foram encontradas.");
		}

		return new HashSet<>(foundTags);
	}
}
