package org.fpcm.util.passwordmeter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Inputs {

	private Addictions addictions;
	private Deductions deductions;
	private Controls controls;

	public Inputs(String typedText) {
		this.addictions = new Addictions(typedText);
		this.deductions = new Deductions();
		this.controls = new Controls();
	}
}
