package org.fpcm.model.dtos;

import lombok.Getter;
import lombok.Setter;
import org.fpcm.model.enums.PasswordComplexityEnum;

@Getter
@Setter
public class PasswordComplexity {

	private PasswordComplexityEnum id;
	private String label;

	public PasswordComplexity(PasswordComplexityEnum complexityEnum) {
		this.id = complexityEnum;
		this.label = complexityEnum.getName();
	}
}
