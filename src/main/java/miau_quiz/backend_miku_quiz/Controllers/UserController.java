package miau_quiz.backend_miku_quiz.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import miau_quiz.backend_miku_quiz.Enums.Visibility;
import miau_quiz.backend_miku_quiz.dto.PersonalInfoDTO;
import miau_quiz.backend_miku_quiz.dto.QuizRatingQuestionsDTO;
import miau_quiz.backend_miku_quiz.dto.UpdateBioUserDTO;
import miau_quiz.backend_miku_quiz.entity.User;
import miau_quiz.backend_miku_quiz.mappers.PersonalInfoMapper;
import miau_quiz.backend_miku_quiz.security.CustomAuthentication;
import miau_quiz.backend_miku_quiz.service.QuizService;
import miau_quiz.backend_miku_quiz.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService service;
	private final PersonalInfoMapper personalInfoMapper;
	private final QuizService quizService;
	
	@GetMapping("/me")
	public ResponseEntity<PersonalInfoDTO> getMyProfile(){
		User currentUser = getCurrentUser();
		PersonalInfoDTO personalInfo = service.getPersonalDashboard(currentUser.getUserId());
		
		return ResponseEntity.ok(personalInfo);
		
	}
	
	@GetMapping("/me/quizzes")
	public ResponseEntity<List<QuizRatingQuestionsDTO>> getMyQuizzes(
			@RequestParam(required=false) String statusQuiz){
		
		
		User currentUser = getCurrentUser();
		List<QuizRatingQuestionsDTO> quizzes = quizService.getPersonalQuizzes(currentUser.getUserId(), statusQuiz);
		return ResponseEntity.ok(quizzes);
	}
	
	@PutMapping("/me")
	public ResponseEntity<PersonalInfoDTO> updateMyProfile(@RequestBody @Valid UpdateBioUserDTO dto){
		User currentUser = getCurrentUser();
		User updateUser = service.updateUser(currentUser.getUserId(), dto);
		PersonalInfoDTO returnDTO = personalInfoMapper.toDTO(updateUser);
		return ResponseEntity.ok(returnDTO);
	}
	
	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteAccount(){
		User currentUser = getCurrentUser();
		service.deleteUser(currentUser.getUserId());
		return ResponseEntity.noContent().build();
	}

	private User getCurrentUser() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication instanceof CustomAuthentication) {
			return ((CustomAuthentication) authentication).getUser();
		}
		
		throw new IllegalStateException("A autenticação atual não contém os detalhes do usuario.");
	}
	
}
