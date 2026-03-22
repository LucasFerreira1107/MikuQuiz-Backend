package miau_quiz.backend_miku_quiz.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import miau_quiz.backend_miku_quiz.dto.RankingEntryDTO;
import miau_quiz.backend_miku_quiz.service.RankingService;

@RestController
@RequestMapping("/api/rankings")
@RequiredArgsConstructor
public class RankingController {

	private final RankingService service;
	
	@GetMapping("/global")
	public ResponseEntity<List<RankingEntryDTO>> getGlobalRanking(){
		List<RankingEntryDTO> ranking = service.getGlobalRanking();
		return ResponseEntity.ok(ranking);
	}
	
	@GetMapping("/quiz/{id}")
	public ResponseEntity<List<RankingEntryDTO>> getQuizRanking(@PathVariable String id){
		List<RankingEntryDTO> ranking = service.getRankingForQuiz(id);
		return ResponseEntity.ok(ranking);
	}
}
