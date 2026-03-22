package miau_quiz.backend_miku_quiz.entity;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name ="question")
@Data
public class Question {

	@Id
	@Column(name = "question_id")
	@GeneratedValue(strategy = GenerationType.UUID)
	public UUID questionId;
	
	@Column(name = "text")
	private String text;
	
	@JsonIncludeProperties({"quizId", "title"})
	@ManyToOne
	@JoinColumn(name = "quiz_id")
	private Quiz quiz;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy= "question", orphanRemoval = true)
	private List<Answer> answers;
}
