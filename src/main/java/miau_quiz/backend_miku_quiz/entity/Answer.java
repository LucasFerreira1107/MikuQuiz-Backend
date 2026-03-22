package miau_quiz.backend_miku_quiz.entity;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "answer")
@Data
public class Answer {

	@Id
	@Column(name = "answer_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID answerId;

	@NotBlank
	@Column(name = "text", nullable = false)
	private String text;
	
	@Column(name = "correct", nullable = false)
	private boolean correct;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "question_id", nullable = false)
	private Question question;
	
	
	@Column(name="explanation")
	private String explanation;
}

