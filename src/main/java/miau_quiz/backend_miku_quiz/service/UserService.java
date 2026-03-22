package miau_quiz.backend_miku_quiz.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import miau_quiz.backend_miku_quiz.dto.PersonalInfoDTO;
import miau_quiz.backend_miku_quiz.dto.UpdateBioUserDTO;
import miau_quiz.backend_miku_quiz.entity.Attempt;
import miau_quiz.backend_miku_quiz.entity.User;
import miau_quiz.backend_miku_quiz.mappers.PersonalInfoMapper;
import miau_quiz.backend_miku_quiz.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder encoder;
	private final LevelService levelService;
	
	@Transactional
	public void save(User user) {
		var password = user.getPassword();
		user.setPassword(encoder.encode(password));
		
		// Inicializa valores padrão para novos usuários
		if (user.getLevel() == null) {
			user.setLevel(1L);
		}
		if (user.getExperience() == null) {
			user.setExperience(0L);
		}
		if (user.getNextLevelExp() == null) {
			user.setNextLevelExp(levelService.calculateExperienceToNextLevel(0));
		}
		
		userRepository.save(user);
	}
	
	@Transactional
	public User updateUser(UUID idUser, UpdateBioUserDTO dto) {
		User user = userRepository.findById(idUser)
				.orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));
		
		// Atualiza apenas os campos que foram fornecidos no DTO
        if (dto.name() != null && !dto.name().isBlank()) {
            user.setName(dto.name());
        }
        if (dto.avatarUrl() != null) {
            user.setAvatarUrl(dto.avatarUrl());
        }
        if (dto.bio() != null) {
            user.setBio(dto.bio());
        }
        
        return userRepository.save(user);
	}
	
	@Transactional
	public void deleteUser(UUID idUser) {
		if(!userRepository.existsById(idUser)) {
			throw new RuntimeException("Usuario não encontrado");
		}
		
		userRepository.deleteById(idUser);
	}
	
	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	public User getUserByLogin(String login) {
		return userRepository.findByEmail(login);
	}
	
	@Transactional()
	public PersonalInfoDTO getPersonalDashboard(UUID userId) {
		User user = userRepository.findById(userId).orElse(null);
		
		User updatedUser = findStatics(user);
		
		return new PersonalInfoDTO(
				updatedUser.getName(), 
				updatedUser.getEmail(),
				updatedUser.getBio(),
				updatedUser.getAvatarUrl(),
				updatedUser.getLevel(),
				updatedUser.getExperience(),
				updatedUser.getNextLevelExp(),
				updatedUser.getQuizzesCreated(), 
				updatedUser.getQuizzesPlayed(), 
				updatedUser.getTotalPoints(),
				updatedUser.getAccuracyPercent(), 
				updatedUser.getMaxStreak());
		
	}
	
	public User findStatics(User user) {
		List<Attempt> attempts = user.getAttempts();
		Long quizzesPlayed = (long) attempts.size();
		Long quizzesCreated = (long) (user.getQuizzesUser() != null ? user.getQuizzesUser().size() : 0);
		
		Long totalPoints = attempts.stream()
				.mapToLong(attempt -> attempt.getScore() != null ? attempt.getScore() : 0)
				.sum();
		
		Double accuracyPercent = attempts.stream()
				.mapToDouble(attempt -> attempt.getAccuracy() != null ? attempt.getAccuracy() : 0.0)
				.average().orElse(0.0);
		
		Long maxStreak = attempts.stream()
				.mapToLong(attempt -> attempt.getMaxStreak() != null ? attempt.getMaxStreak() : 0)
				.max().orElse(0);
		
		// Calcula experiência total baseada nas tentativas
		Long totalExperience = attempts.stream()
				.mapToLong(attempt -> {
					// Calcula XP baseado no desempenho da tentativa
					int score = attempt.getScore() != null ? attempt.getScore() : 0;
					double accuracy = attempt.getAccuracy() != null ? attempt.getAccuracy() : 0.0;
					long streak = attempt.getMaxStreak() != null ? attempt.getMaxStreak() : 0;
					
					// Calcula pontuação máxima baseada no número de questões
					int maxPossibleScore = attempt.getQuiz().getQuestions().size() * 100;
					return levelService.calculateExperienceGained(score, maxPossibleScore, accuracy, streak);
				})
				.sum();
		
		// Calcula nível baseado na experiência total
		Long currentLevel = levelService.calculateLevelFromExperience(totalExperience);
		Long experienceToNextLevel = levelService.calculateExperienceToNextLevel(totalExperience);
		
		// Atualiza as estatísticas do usuário
		user.setTotalPoints(totalPoints);
		user.setAccuracyPercent(accuracyPercent);
		user.setMaxStreak(maxStreak);
		user.setQuizzesPlayed(quizzesPlayed);
		user.setQuizzesCreated(quizzesCreated);
		user.setExperience(totalExperience);
		user.setLevel(currentLevel);
		user.setNextLevelExp(experienceToNextLevel);
		
		return userRepository.save(user);
	}
	
	/**
	 * Atualiza a experiência do usuário após completar um quiz
	 * @param userId ID do usuário
	 * @param score Pontuação obtida no quiz
	 * @param maxPossibleScore Pontuação máxima possível
	 * @param accuracy Precisão do usuário (0.0 a 1.0)
	 * @param maxStreak Streak máximo alcançado
	 * @return true se o usuário subiu de nível, false caso contrário
	 */
	@Transactional
	public boolean updateUserExperience(UUID userId, int score, int maxPossibleScore, double accuracy, long maxStreak) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
		
		// Calcula experiência ganha nesta tentativa
		long experienceGained = levelService.calculateExperienceGained(score, maxPossibleScore, accuracy, maxStreak);
		
		// Obtém nível atual antes da atualização
		long oldLevel = user.getLevel() != null ? user.getLevel() : 1;
		
		// Atualiza experiência total
		long currentExperience = user.getExperience() != null ? user.getExperience() : 0;
		long newTotalExperience = currentExperience + experienceGained;
		
		// Calcula novo nível e experiência para próximo nível
		long newLevel = levelService.calculateLevelFromExperience(newTotalExperience);
		long experienceToNextLevel = levelService.calculateExperienceToNextLevel(newTotalExperience);
		
		// Atualiza o usuário
		user.setExperience(newTotalExperience);
		user.setLevel(newLevel);
		user.setNextLevelExp(experienceToNextLevel);
		
		userRepository.save(user);
		
		// Retorna se houve level up
		return levelService.didLevelUp(oldLevel, newLevel);
	}
	
}
