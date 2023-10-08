package org.fpcm.util.passwordmeter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Options {
	int minLen;
	int multSeqAlpha;
	int multSeqNumber;
	int multSeqSymbol;
	int multLength;
	int multNumber;
	int multSymbol;
	int multMidChar;
	int multConsecAlphaUC;
	int multConsecAlphaLC;
	int multConsecNumber;
}
