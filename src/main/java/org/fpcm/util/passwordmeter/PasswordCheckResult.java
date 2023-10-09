package org.fpcm.util.passwordmeter;

import lombok.Getter;
import lombok.Setter;
import org.fpcm.model.enums.PasswordComplexityEnum;

@Getter
@Setter
public class PasswordCheckResult {

	private int score;
	private PasswordComplexityEnum complexity;
	private PasswordCheckCount count;
	private PasswordCheckBonus bonus;

	public PasswordCheckResult(String typedText, PasswordCheckOptions options){
		this.count = new PasswordCheckCount(typedText);
		this.bonus = new PasswordCheckBonus();
		this.bonus.getAddictions().setNumberOfChars(typedText.length() * options.getMultLength());
		this.score = bonus.getAddictions().getNumberOfChars();
	}
}
