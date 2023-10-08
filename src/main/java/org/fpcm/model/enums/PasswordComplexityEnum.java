package org.fpcm.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PasswordComplexityEnum {

	VERY_WEAK("Very Weak", 20),
	WEAK("Weak", 40),
	GOOD("Good", 60),
	STRONG("Strong", 80),
	VERY_STRONG("Very Strong", 100);

	private final String name;
	private final int minScore;

	public static PasswordComplexityEnum getByScore(int passwordScore) {
		for(PasswordComplexityEnum e : values()){
			if(passwordScore < e.getMinScore()){
				return e;
			}
		}
		return VERY_STRONG; // after 100.
	}
}
