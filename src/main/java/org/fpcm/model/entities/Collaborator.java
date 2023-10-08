package org.fpcm.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.fpcm.model.enums.PasswordStrengthEnum;

@Entity
@Getter
@Setter
public class Collaborator {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private int id;

	private String name;

	private String password;

	@Transient
	private PasswordStrengthEnum passStrength;

	private int passwordScore;

	private String path;

	@ManyToOne
	private Collaborator manager;

	public PasswordStrengthEnum getPassStrength() {
		return PasswordStrengthEnum.getByScore(this.getPasswordScore());
	}
}
