package miau_quiz.backend_miku_quiz.dto;

import java.util.UUID;

public record RankingEntryDTO(Integer position,UUID userId, String userName, String avatarUrl, Integer score, Double accuracy, Integer streak) {

	public RankingEntryDTO(UUID userId, String userName, String avatarUrl, Long score, Double accuracy, Integer streak) {
        this(null, userId, userName, avatarUrl, score != null ? score.intValue() : 0, accuracy,  streak);
    }

	// Construtor para a query de Ranking por Quiz (MAX)
    public RankingEntryDTO(UUID userId, String userName, String avatarUrl, Integer score, Double accuracy, Integer streak) {
        this(null, userId, userName, avatarUrl, score != null ? score : 0, accuracy,  streak);
    }

 // Método para criar uma nova instância com a posição definida
    public RankingEntryDTO withPosition(int position) {
        return new RankingEntryDTO(position, this.userId, this.userName, this.avatarUrl, this.score, this.accuracy,  this.streak);
    }

}
