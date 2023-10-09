package org.fpcm.util.passwordmeter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordCheckBonus {
	private PasswordCheckAddictions addictions;
	private PasswordCheckDeductions deductions;

	public PasswordCheckBonus(){
		this.addictions = new PasswordCheckAddictions();
		this.deductions = new PasswordCheckDeductions();
	}
}
