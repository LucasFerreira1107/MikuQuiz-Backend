package miau_quiz.backend_miku_quiz.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import miau_quiz.backend_miku_quiz.Enums.AuthProvider;

@Entity
@Table(name = "users")
@Data
@EntityListeners(AuditingEntityListener.class)
public class User {

	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID userId;
	
	@Column(name = "name", length = 200, nullable = false)
	private String name;
	
	@Column(name = "email", unique = true, length = 200, nullable = false)
	private String email;
	
	@Column(name = "password", nullable = false)
	private String password;
	
	@Column(name = "login", unique = true)
	private String login;

	@ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
	private List<String> roles;
	
	@Column(name = "avatar_url")
	private String avatarUrl;
	
	@Column(name = "bio")
	private String bio;
	
	@Enumerated(EnumType.STRING)
	@Column(name ="provider")
	private AuthProvider provider;
	
	@Column(name ="level")
	private Long level;
	
	@Column(name ="experience")
	private Long experience;
	
	@Column(name ="next_level_exp")
	private Long  nextLevelExp;
	
	@Column(name ="quizzes_played")
	private Long quizzesPlayed;
	
	@Column(name ="quizzes_created")
	private Long quizzesCreated;
	
	@Column(name ="accuracy_percent")
	private Double accuracyPercent;
	
	@Column(name ="maxStreak")
	private Long maxStreak;
	
	@Column(name ="total_points")
	private Long totalPoints;
	
	@CreatedDate
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@LastModifiedDate
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
	@Column(name = "reset_password_token")
	private String resetPasswordToken;
	
	@Column(name = "reset_password_token_expiry")
	private LocalDateTime resetPasswordTokenExpiry;
	
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private List<Quiz> quizzesUser;
	
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private List<Attempt> attempts;
}
