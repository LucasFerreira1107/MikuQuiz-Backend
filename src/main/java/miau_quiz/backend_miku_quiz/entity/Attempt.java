package miau_quiz.backend_miku_quiz.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name ="attempt")
@Data
public class Attempt {

	@Id
	@Column(name = "attempt_id")
	@GeneratedValue(strategy = GenerationType.UUID)
	public UUID attemptId;
	
	@Column(name ="score")
	private Integer score;
	
	@Column(name ="accuracy")
	private Double accuracy;
	
	@Column(name ="max_streak")
	private Integer maxStreak;
	
	
	@Column(name ="rating")
	private int rating;
	
	@ManyToOne
	@JoinColumn(name = "quiz_id")
	private Quiz quiz;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
}
