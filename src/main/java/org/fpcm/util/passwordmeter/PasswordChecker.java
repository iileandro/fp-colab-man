package org.fpcm.util.passwordmeter;

import org.fpcm.model.enums.PasswordComplexityEnum;

import java.util.regex.Pattern;

/**
 *   Based on code hosted at https://passwordmeter.com/js/pwdmeter.js
 *   Refactoring by Leandro Santana.
 *   Credits to Jeff Todnem (http://www.todnem.com/)
 *   Copyright (C) 2007 Jeff Todnem
 */
public class PasswordChecker {

	private static final PasswordChecker INSTANCE = new PasswordChecker();

	private static final String ALPHAS = "abcdefghijklmnopqrstuvwxyz";
	private static final String NUMERICS = "01234567890";
	private static final String SYMBOLS = ")!@#$%^&*()";

	public static PasswordChecker getInstance() {
		return INSTANCE;
	}

	private PasswordChecker() {
		// Just to be private.
	}

	public PasswordCheckResult execute(String typedTxt) {
		return execute(typedTxt,  new PasswordCheckOptions());
	}

	public PasswordCheckResult execute(String typedTxt, PasswordCheckOptions options) {
		PasswordCheckResult result = null;

		if (typedTxt != null && !typedTxt.isEmpty()) {
			result = new PasswordCheckResult(typedTxt, options);
			checkAndRankSymbolsNumericLowerAndUpper(typedTxt, result);
			checkAndRankSequentials(typedTxt, result, options);
			modifyOverallScore(result, options);
			calculateRequirements(typedTxt, result, options);
			calculateFinalScoreAndComplexity(result);
		}

		return result;
	}

	private void checkAndRankSymbolsNumericLowerAndUpper(String typedTxt, PasswordCheckResult result) {
		char[] arrPwd = typedTxt.replaceAll("\\s+", "").toCharArray();
		int arrPwdLen = arrPwd.length;
		Integer nTmpAlphaUC = null, nTmpAlphaLC = null, nTmpNumber = null;

		for (int a = 0; a < arrPwdLen; a++) {
			char currentChar = arrPwd[a];
			String currentCharStr = String.valueOf(currentChar);

			if (Pattern.matches("[A-Z]", currentCharStr)) {
				if (nTmpAlphaUC != null && (nTmpAlphaUC + 1) == a) {
					result.getCount().getDeductions().setConsecutiveUpper(result.getCount().getDeductions().getConsecutiveUpper() + 1);
				}
				nTmpAlphaUC = a;
				result.getCount().getAddictions().setUpperLetters(result.getCount().getAddictions().getUpperLetters() + 1);
			} else if (Pattern.matches("[a-z]", currentCharStr)) {
				if (nTmpAlphaLC != null && (nTmpAlphaLC + 1) == a) {
					result.getCount().getDeductions().setConsecutiveLower(result.getCount().getDeductions().getConsecutiveLower() + 1);
				}
				nTmpAlphaLC = a;
				result.getCount().getAddictions().setLowerLetters(result.getCount().getAddictions().getLowerLetters() + 1);
			} else if (Pattern.matches("[0-9]", currentCharStr)) {
				if (a > 0 && a < (arrPwdLen - 1)) {
					result.getCount().getAddictions().setMiddleNumbersOrSymbols(result.getCount().getAddictions().getMiddleNumbersOrSymbols() + 1);
				}
				if (nTmpNumber != null && (nTmpNumber + 1) == a) {
					result.getCount().getDeductions().setConsecutiveNumbers(result.getCount().getDeductions().getConsecutiveNumbers() + 1);
				}
				nTmpNumber = a;
				result.getCount().getAddictions().setNumbers(result.getCount().getAddictions().getNumbers() + 1);
			} else if (Pattern.matches("[^a-zA-Z0-9_]", currentCharStr)) {
				if (a > 0 && a < (arrPwdLen - 1)) {
					result.getCount().getAddictions().setMiddleNumbersOrSymbols(result.getCount().getAddictions().getMiddleNumbersOrSymbols() + 1);
				}
				result.getCount().getAddictions().setSymbols(result.getCount().getAddictions().getSymbols() + 1);
			}

			boolean bCharExists = false;
			for (int b = 0; b < arrPwdLen; b++) {
				if (currentChar == arrPwd[b] && a != b) {
					bCharExists = true;
					result.getCount().getControls().setNRepInc(result.getCount().getControls().getNRepInc() + Math.abs(((double) arrPwdLen / (double)(b - a))));
				}
			}
			if (bCharExists) {
				result.getCount().getDeductions().setRepeatChars(result.getCount().getDeductions().getRepeatChars() + 1);
				int nUnqChar = arrPwdLen - result.getCount().getDeductions().getRepeatChars();
				result.getCount().getControls().setNRepInc(
							((nUnqChar > 0)
								?
								Math.ceil(result.getCount().getControls().getNRepInc() / nUnqChar)
								:
								Math.ceil(result.getCount().getControls().getNRepInc())
						)
				);
			}
		}
	}

	private void checkAndRankSequentials(String typedTxt, PasswordCheckResult result, PasswordCheckOptions options) {
		for (int s = 0; s < ALPHAS.length() - options.getMultSeqAlpha(); s++) {
			String sFwd = limitedSubstr(ALPHAS, s, options.getMultSeqAlpha());
			String sRev = new StringBuilder(sFwd).reverse().toString();
			if (typedTxt.toLowerCase().contains(sFwd) || typedTxt.toLowerCase().contains(sRev)) {
				result.getCount().getDeductions().setSequentialLetters(result.getCount().getDeductions().getSequentialLetters() + 1);
				result.getCount().getControls().setNSeqChar(result.getCount().getControls().getNSeqChar() + 1);
			}
		}

		for (int s = 0; s < NUMERICS.length() - options.getMultSeqNumber(); s++) {
			String sFwd = limitedSubstr(NUMERICS, s, options.getMultSeqNumber());
			String sRev = new StringBuilder(sFwd).reverse().toString();
			if (typedTxt.toLowerCase().contains(sFwd) || typedTxt.toLowerCase().contains(sRev)) {
				result.getCount().getDeductions().setSequentialNumbers(result.getCount().getDeductions().getSequentialNumbers() + 1);
				result.getCount().getControls().setNSeqChar(result.getCount().getControls().getNSeqChar() + 1);
			}
		}

		for (int s = 0; s < SYMBOLS.length() - options.getMultSeqSymbol(); s++) {
			String sFwd = limitedSubstr(SYMBOLS, s, options.getMultSeqSymbol());
			String sRev = new StringBuilder(sFwd).reverse().toString();
			if (typedTxt.toLowerCase().contains(sFwd) || typedTxt.toLowerCase().contains(sRev)) {
				result.getCount().getDeductions().setSequentialSymbols(result.getCount().getDeductions().getSequentialSymbols() + 1);
				result.getCount().getControls().setNSeqChar(result.getCount().getControls().getNSeqChar() + 1);
			}
		}
	}

	private String limitedSubstr(String context, int s, int mult) {
		return context.substring(s, s + mult);
	}

	private void modifyOverallScore(PasswordCheckResult result, PasswordCheckOptions options) {
		PasswordCheckAddictions addictions = result.getCount().getAddictions();
		PasswordCheckDeductions deductions = result.getCount().getDeductions();

		// ADDICTIONS...
		if (addictions.getUpperLetters() > 0 && addictions.getUpperLetters() < addictions.getNumberOfChars()) {
			result.getBonus().getAddictions().setUpperLetters(((addictions.getNumberOfChars() - addictions.getUpperLetters()) * 2));
			result.setScore(result.getScore() + result.getBonus().getAddictions().getUpperLetters());
		}
		if (addictions.getLowerLetters() > 0 && addictions.getLowerLetters() < addictions.getNumberOfChars()) {
			result.getBonus().getAddictions().setLowerLetters(((addictions.getNumberOfChars() - addictions.getLowerLetters()) * 2));
			result.setScore(result.getScore() + result.getBonus().getAddictions().getLowerLetters());
		}
		if (addictions.getNumbers() > 0 && addictions.getNumbers() < addictions.getNumberOfChars()) {
			result.getBonus().getAddictions().setNumbers((addictions.getNumbers() * options.getMultNumber()));
			result.setScore(result.getScore() + result.getBonus().getAddictions().getNumbers());
		}
		if (addictions.getSymbols() > 0) {
			result.getBonus().getAddictions().setSymbols((addictions.getSymbols() * options.getMultSymbol()));
			result.setScore(result.getScore() + result.getBonus().getAddictions().getSymbols());
		}
		if (addictions.getMiddleNumbersOrSymbols() > 0) {
			result.getBonus().getAddictions().setMiddleNumbersOrSymbols(addictions.getMiddleNumbersOrSymbols() * options.getMultMidChar());
			result.setScore(result.getScore() + result.getBonus().getAddictions().getMiddleNumbersOrSymbols());
		}


		// DEDUCTIONS...

		if ((addictions.getLowerLetters() > 0 || addictions.getUpperLetters() > 0) && addictions.getMiddleNumbersOrSymbols() == 0 && addictions.getNumbers() == 0) {
			result.getBonus().getDeductions().setLettersOnly(-addictions.getNumberOfChars());
			result.setScore(result.getScore() + result.getBonus().getDeductions().getLettersOnly());
			deductions.setLettersOnly(addictions.getNumberOfChars());
		}
		if (addictions.getLowerLetters() == 0 && addictions.getUpperLetters() == 0 && addictions.getSymbols() == 0 && addictions.getNumbers() > 0) {
			result.getBonus().getDeductions().setNumbersOnly(-addictions.getNumberOfChars());
			result.setScore(result.getScore() + result.getBonus().getDeductions().getNumbersOnly());
			deductions.setNumbersOnly(addictions.getNumberOfChars());
		}

		if (deductions.getRepeatChars() > 0) {
			result.getBonus().getDeductions().setRepeatChars((int) -result.getCount().getControls().getNRepInc());
			result.setScore(result.getScore() + result.getBonus().getDeductions().getRepeatChars());
		}

		if (deductions.getConsecutiveUpper() > 0) {
			result.getBonus().getDeductions().setConsecutiveUpper(-(deductions.getConsecutiveUpper() * options.getMultConsecAlphaUC()));
			result.setScore(result.getScore() + result.getBonus().getDeductions().getConsecutiveUpper());
		}
		if (deductions.getConsecutiveLower() > 0) {
			result.getBonus().getDeductions().setConsecutiveLower(-(deductions.getConsecutiveLower() * options.getMultConsecAlphaLC()));
			result.setScore(result.getScore() + result.getBonus().getDeductions().getConsecutiveLower());
		}
		if (deductions.getConsecutiveNumbers() > 0) {
			result.getBonus().getDeductions().setConsecutiveNumbers(-(deductions.getConsecutiveNumbers() * options.getMultConsecNumber()));
			result.setScore(result.getScore() + result.getBonus().getDeductions().getConsecutiveNumbers());
		}

		if (deductions.getSequentialLetters() > 0) {
			result.getBonus().getDeductions().setSequentialLetters(-(deductions.getSequentialLetters() * options.getMultSeqAlpha()));
			result.setScore(result.getScore() + result.getBonus().getDeductions().getSequentialLetters());
		}
		if (deductions.getSequentialNumbers() > 0) {
			result.getBonus().getDeductions().setSequentialNumbers(-(deductions.getSequentialNumbers() * options.getMultSeqNumber()));
			result.setScore(result.getScore() + result.getBonus().getDeductions().getSequentialNumbers());
		}
		if (deductions.getSequentialSymbols() > 0) {
			result.getBonus().getDeductions().setSequentialSymbols(-(deductions.getSequentialSymbols() * options.getMultSeqSymbol()));
			result.setScore(result.getScore() + result.getBonus().getDeductions().getSequentialSymbols());
		}
	}

	private void calculateFinalScoreAndComplexity(PasswordCheckResult result) {
		int nScore = result.getScore();
		if (nScore > 100) {
			nScore = 100;
		} else if (nScore < 0) {
			nScore = 0;
		}
		result.setScore(nScore);
		result.setComplexity(PasswordComplexityEnum.getByScore(result.getScore()));
	}

	private void calculateRequirements(String typedText, PasswordCheckResult result, PasswordCheckOptions options) {
		PasswordCheckAddictions addictions = result.getCount().getAddictions();

		int[] arrChars = {
				addictions.getNumberOfChars(),
				addictions.getUpperLetters(),
				addictions.getLowerLetters(),
				addictions.getNumbers(),
				addictions.getSymbols()
		};
		String[] arrCharsIds = {
				"numberOfChars", "upperLetters", "lowerLetters", "numbers", "symbols"
		};
		int arrCharsLen = arrChars.length;
		int nMinReqChars = (typedText.length() >= options.getMinLen()) ? 3 : 4;

		for (int c = 0; c < arrCharsLen; c++) {
			int minVal = (arrCharsIds[c].equals("numberOfChars")) ? options.getMinLen() - 1 : 0;
			if (arrChars[c] == minVal + 1) {
				result.getCount().getControls().setNReqChar(result.getCount().getControls().getNReqChar() + 1);
			} else if (arrChars[c] > (minVal + 1)) {
				result.getCount().getControls().setNReqChar(result.getCount().getControls().getNReqChar() + 1);
			}
		}
		addictions.setRequirements(result.getCount().getControls().getNReqChar());

		if (addictions.getRequirements() > nMinReqChars) {
			result.setScore(result.getScore() + (addictions.getRequirements() * 2));
		}
	}

	public static void main(String[] args) {
		String password = "9876543210";
//		String password = "29323943423492234249234452452";
		PasswordCheckResult result = PasswordChecker.getInstance().execute(password);
		System.out.println("Score: " + result.getScore());
		System.out.println("Complexity: " + result.getComplexity());
	}
}
