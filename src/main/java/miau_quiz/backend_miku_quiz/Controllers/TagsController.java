package miau_quiz.backend_miku_quiz.Controllers;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import miau_quiz.backend_miku_quiz.entity.Tags;
import miau_quiz.backend_miku_quiz.service.TagsService;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagsController {

	private final TagsService service;
	
	@GetMapping
	public ResponseEntity<List<Tags>> getTags(){
		List<Tags> tags = service.getTags();
		return ResponseEntity.ok(tags);
	}
}
