package org.fpcm.util.passwordmeter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordCheckDeductions {

	private int lettersOnly;
	private int numbersOnly;
	private int repeatChars;
	private int consecutiveUpper;
	private int consecutiveLower;
	private int consecutiveNumbers;
	private int sequentialLetters;
	private int sequentialNumbers;
	private int sequentialSymbols;
}
