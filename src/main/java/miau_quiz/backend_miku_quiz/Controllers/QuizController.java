package miau_quiz.backend_miku_quiz.Controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import miau_quiz.backend_miku_quiz.dto.AttemptFormDTO;
import miau_quiz.backend_miku_quiz.dto.AttemptResultDTO;
import miau_quiz.backend_miku_quiz.dto.QuizCreateDTO;
import miau_quiz.backend_miku_quiz.dto.QuizRatingDTO;
import miau_quiz.backend_miku_quiz.dto.QuizRatingQuestionsDTO;
import miau_quiz.backend_miku_quiz.dto.QuizUpdateDTO;
import miau_quiz.backend_miku_quiz.entity.Quiz;
import miau_quiz.backend_miku_quiz.entity.User;
import miau_quiz.backend_miku_quiz.security.CustomAuthentication;
import miau_quiz.backend_miku_quiz.service.AttemptService;
import miau_quiz.backend_miku_quiz.service.QuizService;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {
	

	private final QuizService quizService;
	private final AttemptService attemptService;
	
	@GetMapping("/{id}")
	public @ResponseBody QuizRatingQuestionsDTO getQuizById(@PathVariable("id")String id) {
		return quizService.getQuizById(id);
	}
	
	@GetMapping
	public @ResponseBody List<QuizRatingQuestionsDTO> getQuizzes(
			@RequestParam(name="q", required=false) String query,
			@RequestParam(required=false) String difficulty,
			@RequestParam(required=false) String tagsId) {
		
		return quizService.getQuizzes(query, difficulty, tagsId);
	}
	
	@PostMapping
	public ResponseEntity<?> createQuiz(@RequestBody QuizCreateDTO quizDTO, Authentication authentication){
		
		User currentUser = ((CustomAuthentication) authentication).getUser();
		Quiz createdQuiz = quizService.createQuiz(quizDTO,currentUser);
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(createdQuiz.getId()).toUri();
		return ResponseEntity.created(location).body(createdQuiz);
		
		
	}
	
	@PostMapping("/{id}/submit")
	public ResponseEntity<AttemptResultDTO> submitQuizResult(
			@PathVariable("id")String id, @RequestBody @Valid AttemptFormDTO dto,
			Authentication authentication){
		
		User currentUser = ((CustomAuthentication) authentication).getUser();
		AttemptResultDTO result = attemptService.sendAttempt(dto, id, currentUser);
		
		return ResponseEntity.ok(result);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> updateQuiz(@PathVariable("id") String id, @RequestBody @Valid QuizUpdateDTO dto, Authentication authentication){
		User currentUser = ((CustomAuthentication) authentication).getUser();
		
		Quiz updatedQuiz = quizService.updateQuiz(id, dto, currentUser);
		return ResponseEntity.ok(updatedQuiz);
	}
	
	
	@DeleteMapping("{id}")
	public ResponseEntity<?> deleteQuiz(@PathVariable("id")String id, Authentication authentication){
		
		User currentUser = ((CustomAuthentication) authentication).getUser();
		quizService.deleteQuizById(id, currentUser);
		return ResponseEntity.noContent().build();
	}
}
