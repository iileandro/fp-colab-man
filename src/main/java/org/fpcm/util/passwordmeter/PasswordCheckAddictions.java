package org.fpcm.util.passwordmeter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordCheckAddictions {

	private int numberOfChars;
	private int upperLetters;
	private int lowerLetters;
	private int numbers;
	private int symbols;
	private int middleNumbersOrSymbols;
	private int requirements;

	public PasswordCheckAddictions(String typedTxt) {
		this.numberOfChars = typedTxt.length();
	}
}
