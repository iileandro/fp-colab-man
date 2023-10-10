package org.fpcm.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.fpcm.model.dtos.PasswordComplexity;
import org.fpcm.model.enums.PasswordComplexityEnum;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;

@Entity
@Getter@Setter
public class Collaborator {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private int id;

	private String name;

	@Transient
	private String plainPassword;

	private String encryptedPassword;

	@Transient
	private PasswordComplexity passComplexity;

	private int passwordScore;

	@Lob
	private String treePath;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	private Collaborator manager;

	@Column(name="manager_id", insertable = false, updatable = false)
	private Integer managerId;

	@OneToMany(mappedBy = "manager")
	@Fetch(FetchMode.JOIN)
	private List<Collaborator> managedCollaborators;


	public PasswordComplexity getPassComplexity() {
		return new PasswordComplexity(PasswordComplexityEnum.getByScore(this.getPasswordScore()));
	}

	@JsonIgnore
	public boolean isValidForCreate() {
		return !StringUtils.isEmpty(this.getName()) && !StringUtils.isEmpty(this.getPlainPassword());
	}

	@JsonIgnore
	public boolean isValidForUpdate() {
		return !StringUtils.isEmpty(this.getName());
	}

	public void fillFromUpdate(Collaborator collaboratorFromForm) {
		this.setName(collaboratorFromForm.getName());
		this.setPlainPassword(collaboratorFromForm.getPlainPassword());
	}
}
