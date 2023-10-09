package org.fpcm.util.passwordmeter;

import lombok.Getter;
import lombok.Setter;
import org.fpcm.model.enums.PasswordComplexityEnum;

@Getter
@Setter
public class PasswordCheckResult {

	private int score;
	private PasswordComplexityEnum complexity;
	private PasswordCheckInputs inputs;

	public PasswordCheckResult(String typedText, PasswordCheckOptions options){
		this.score = typedText.length() * options.getMultLength();
		this.inputs = new PasswordCheckInputs(typedText);
	}
}
