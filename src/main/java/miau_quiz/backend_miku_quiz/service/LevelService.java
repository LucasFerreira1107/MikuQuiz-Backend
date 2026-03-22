package miau_quiz.backend_miku_quiz.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LevelService {
    
    // Fórmula para calcular XP necessário para o próximo nível
    // Fórmula: XP = 100 * (nível ^ 1.5)
    public long calculateExperienceForLevel(long level) {
        if (level <= 1) {
            return 0;
        }
        return Math.round(100 * Math.pow(level, 1.5));
    }
    
    // Calcula o nível baseado na experiência atual
    public long calculateLevelFromExperience(long experience) {
        if (experience <= 0) {
            return 1;
        }
        
        long level = 1;
        long totalExpNeeded = 0;
        
        // Encontra o nível correto baseado na experiência
        while (totalExpNeeded <= experience) {
            level++;
            totalExpNeeded += calculateExperienceForLevel(level);
        }
        
        return level - 1;
    }
    
    // Calcula a experiência necessária para o próximo nível
    public long calculateExperienceToNextLevel(long currentExperience) {
        long currentLevel = calculateLevelFromExperience(currentExperience);
        
        // Calcula a experiência total necessária para o próximo nível
        long totalExpForNextLevel = 0;
        for (long i = 2; i <= currentLevel + 1; i++) {
            totalExpForNextLevel += calculateExperienceForLevel(i);
        }
        
        // Calcula a experiência total necessária para o nível atual
        long totalExpForCurrentLevel = 0;
        for (long i = 2; i <= currentLevel; i++) {
            totalExpForCurrentLevel += calculateExperienceForLevel(i);
        }
        
        // Retorna apenas a experiência necessária para o próximo nível
        return totalExpForNextLevel - currentExperience;
    }
    
    // Calcula experiência ganha baseada no desempenho no quiz
    public long calculateExperienceGained(int score, int maxPossibleScore, double accuracy, long maxStreak) {
        if (maxPossibleScore == 0) {
            return 0;
        }
        
        // Base XP: 10 pontos por quiz completado
        long baseXp = 10;
        
        // Bonus por pontuação (0-50 XP baseado na pontuação)
        double scoreRatio = (double) score / maxPossibleScore;
        long scoreBonus = Math.round(50 * scoreRatio);
        
        // Bonus por precisão (0-30 XP baseado na precisão)
        long accuracyBonus = Math.round(30 * accuracy);
        
        // Bonus por streak (0-20 XP baseado no streak máximo)
        long streakBonus = Math.min(maxStreak * 2, 20);
        
        long totalXp = baseXp + scoreBonus + accuracyBonus + streakBonus;
        
        return Math.max(totalXp, 1); // Mínimo 1 XP por tentativa
    }
    
    // Verifica se o usuário subiu de nível
    public boolean didLevelUp(long oldLevel, long newLevel) {
        return newLevel > oldLevel;
    }
}
