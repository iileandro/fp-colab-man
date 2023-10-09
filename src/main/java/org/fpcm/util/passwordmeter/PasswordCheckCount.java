package org.fpcm.util.passwordmeter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordCheckCount {

	private PasswordCheckAddictions addictions;
	private PasswordCheckDeductions deductions;
	private PasswordCheckControls controls;

	public PasswordCheckCount(String typedText) {
		this.addictions = new PasswordCheckAddictions(typedText);
		this.deductions = new PasswordCheckDeductions();
		this.controls = new PasswordCheckControls();
	}
}
