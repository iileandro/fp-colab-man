package org.fpcm.util.passwordmeter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordCheckOptions {
	int minLen = 8;
	int multSeqAlpha = 3;
	int multSeqNumber = 3;
	int multSeqSymbol = 3;
	int multLength = 4;
	int multNumber = 4;
	int multSymbol = 6;
	int multMidChar = 2;
	int multConsecAlphaUC = 2;
	int multConsecAlphaLC = 2;
	int multConsecNumber = 2;

}
