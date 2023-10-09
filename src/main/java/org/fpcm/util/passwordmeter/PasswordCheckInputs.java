package org.fpcm.util.passwordmeter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordCheckInputs {

	private PasswordCheckAddictions addictions;
	private PasswordCheckDeductions deductions;
	private PasswordCheckControls controls;

	public PasswordCheckInputs(String typedText) {
		this.addictions = new PasswordCheckAddictions(typedText);
		this.deductions = new PasswordCheckDeductions();
		this.controls = new PasswordCheckControls();
	}
}
