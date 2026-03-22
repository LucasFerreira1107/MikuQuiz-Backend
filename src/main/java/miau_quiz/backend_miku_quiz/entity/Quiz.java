package miau_quiz.backend_miku_quiz.entity;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import miau_quiz.backend_miku_quiz.Enums.Difficulty;
import miau_quiz.backend_miku_quiz.Enums.QuizStatus;
import miau_quiz.backend_miku_quiz.Enums.TimeLimit;
import miau_quiz.backend_miku_quiz.Enums.Visibility;

@Entity
@Table(name = "quiz")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Quiz {

	@Id
	@Column(name = "quiz_id")
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@JsonIncludeProperties({"id"})
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "description")
	private String description;
	
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty")
	private Difficulty difficulty;
    
    
    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinTable(name = "quiz_tags",
    		 joinColumns = @JoinColumn(name = "quiz_id"), // 2. A coluna nesta tabela que aponta para Quiz
    		  inverseJoinColumns = @JoinColumn(name = "tag_id") // 3. A coluna que aponta para Tags
    )
    private Set<Tags> tags;
    
// 	private Set<String> tagsID;

    @Enumerated (EnumType.STRING)
    @Column(name = "time_question")
	private TimeLimit timePerQuestion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
	private QuizStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
	private Visibility visibility;
    
    @Column(name = "plays_count")
	private long playsCount;
    
    @Column(name = "avg_rating")
	private double avgRating;
    
    @Column(name = "allow_offline")
	private boolean allowOffline; 
    
    @CreatedDate
    @Column(name = "created_at")
	private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
	private LocalDateTime updatedAt;	
	
    @JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "quiz")
	private List<Attempt> attempts;
	
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "quiz", orphanRemoval = true)
	private List<Question> questions;
}

