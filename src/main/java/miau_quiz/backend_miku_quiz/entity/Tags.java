package miau_quiz.backend_miku_quiz.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "tags")
@Data
public class Tags {

	@Id
	@Column(name = "tags_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID tagsId;

	@NotBlank
	@Column(name = "name", nullable = false)
	private String name;
}
