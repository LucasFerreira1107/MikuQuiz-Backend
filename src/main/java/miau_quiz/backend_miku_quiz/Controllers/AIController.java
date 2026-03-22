package miau_quiz.backend_miku_quiz.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import miau_quiz.backend_miku_quiz.dto.AIGenerateQuizRequestDTO;
import miau_quiz.backend_miku_quiz.dto.QuizCreateDTO;
import miau_quiz.backend_miku_quiz.service.AIService;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

	
	private final AIService aiService;
	
	@PostMapping("/generate")
    public ResponseEntity<QuizCreateDTO> generateQuiz(@RequestBody AIGenerateQuizRequestDTO request) {
        QuizCreateDTO generatedQuiz = aiService.generateQuizFromPrompt(request);
        return ResponseEntity.ok(generatedQuiz);
    }
}