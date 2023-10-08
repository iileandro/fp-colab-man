package org.fpcm.util.passwordmeter;

import lombok.Getter;
import lombok.Setter;
import org.fpcm.model.enums.PasswordComplexityEnum;

@Getter
@Setter
public class Result {

	private int score;
	private PasswordComplexityEnum complexity;
	private Inputs inputs;

	public Result(String typedText, Options options){
		this.score = typedText.length() * options.getMultLength();
		this.inputs = new Inputs(typedText);
	}
}
