package miau_quiz.backend_miku_quiz.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import miau_quiz.backend_miku_quiz.dto.RankingEntryDTO;
import miau_quiz.backend_miku_quiz.repository.AttemptRepository;

@Service
@RequiredArgsConstructor
public class RankingService {

	private final AttemptRepository attemptRepository;
	
	@Transactional(readOnly = true)
	public List<RankingEntryDTO> getGlobalRanking(){
		List<RankingEntryDTO>  ranking = attemptRepository.findGlobalRanking();
		
		
		return addPositionsToRanking(ranking);
	}

	/**
     * Método auxiliar para adicionar o número da posição (1º, 2º, 3º...) à lista de ranking.
     */
	private List<RankingEntryDTO> addPositionsToRanking(List<RankingEntryDTO> ranking) {
		return IntStream.range(0, ranking.size())
				.mapToObj(i -> ranking.get(i).withPosition(i+1))
				.collect(Collectors.toList());
	}
	
	@Transactional(readOnly=true)
	public List<RankingEntryDTO> getRankingForQuiz(String quizId){
		UUID quizUUID = UUID.fromString(quizId);
		List<RankingEntryDTO>  ranking = attemptRepository.findRankingForQuiz(quizUUID);
		return addPositionsToRanking(ranking);
	}
}
