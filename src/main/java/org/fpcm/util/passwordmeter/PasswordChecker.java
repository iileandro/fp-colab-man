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
	private static final Options DEFAULT_OPTIONS = new Options(8, 3, 3, 3, 4, 4, 6, 2, 2, 2, 2);

	public static PasswordChecker getInstance() {
		return INSTANCE;
	}

	private PasswordChecker() {
		// Just to be private.
	}

	public Result execute(String typedTxt) {
		return execute(typedTxt, DEFAULT_OPTIONS);
	}

	public Result execute(String typedTxt, Options options) {
		Result result = null;

		if (typedTxt != null && !typedTxt.isEmpty()) {
			result = new Result(typedTxt, options);
			checkAndRankSymbolsNumericLowerAndUpper(typedTxt, result);
			checkAndRankSequentials(typedTxt, result, options);
			modifyOverallScore(result, options);
			calculateRequirements(typedTxt, result, options);
			calculateFinalScoreAndComplexity(result);
		}

		return result;
	}

	private void checkAndRankSymbolsNumericLowerAndUpper(String typedTxt, Result result) {
		char[] arrPwd = typedTxt.replaceAll("\\s+", "").toCharArray();
		int arrPwdLen = arrPwd.length;
		Integer nTmpAlphaUC = null, nTmpAlphaLC = null, nTmpNumber = null;

		for (int a = 0; a < arrPwdLen; a++) {
			char currentChar = arrPwd[a];
			String currentCharStr = String.valueOf(currentChar);

			if (Pattern.matches("[A-Z]", currentCharStr)) {
				if (nTmpAlphaUC != null && (nTmpAlphaUC + 1) == a) {
					result.getInputs().getDeductions().setConsecutiveUpper(result.getInputs().getDeductions().getConsecutiveUpper() + 1);
				}
				nTmpAlphaUC = a;
				result.getInputs().getAddictions().setUpperLetters(result.getInputs().getAddictions().getUpperLetters() + 1);
			} else if (Pattern.matches("[a-z]", currentCharStr)) {
				if (nTmpAlphaLC != null && (nTmpAlphaLC + 1) == a) {
					result.getInputs().getDeductions().setConsecutiveLower(result.getInputs().getDeductions().getConsecutiveLower() + 1);
				}
				nTmpAlphaLC = a;
				result.getInputs().getAddictions().setLowerLetters(result.getInputs().getAddictions().getLowerLetters() + 1);
			} else if (Pattern.matches("[0-9]", currentCharStr)) {
				if (a > 0 && a < (arrPwdLen - 1)) {
					result.getInputs().getAddictions().setMiddleNumbersOrSymbols(result.getInputs().getAddictions().getMiddleNumbersOrSymbols() + 1);
				}
				if (nTmpNumber != null && (nTmpNumber + 1) == a) {
					result.getInputs().getDeductions().setConsecutiveNumbers(result.getInputs().getDeductions().getConsecutiveNumbers() + 1);
				}
				nTmpNumber = a;
				result.getInputs().getAddictions().setNumbers(result.getInputs().getAddictions().getNumbers() + 1);
			} else if (Pattern.matches("[^a-zA-Z0-9_]", currentCharStr)) {
				if (a > 0 && a < (arrPwdLen - 1)) {
					result.getInputs().getAddictions().setMiddleNumbersOrSymbols(result.getInputs().getAddictions().getMiddleNumbersOrSymbols() + 1);
				}
				result.getInputs().getAddictions().setSymbols(result.getInputs().getAddictions().getSymbols() + 1);
			}

			boolean bCharExists = false;
			for (int b = 0; b < arrPwdLen; b++) {
				if (currentChar == arrPwd[b] && a != b) {
					bCharExists = true;
					result.getInputs().getControls().setNRepInc(result.getInputs().getControls().getNRepInc() + Math.abs(arrPwdLen / (b - a)));
				}
			}
			if (bCharExists) {
				result.getInputs().getDeductions().setRepeatChars(result.getInputs().getDeductions().getRepeatChars() + 1);
				int nUnqChar = arrPwdLen - result.getInputs().getDeductions().getRepeatChars();
				result.getInputs().getControls().setNRepInc(
						(int) ((nUnqChar > 0)
								?
								Math.ceil((double) result.getInputs().getControls().getNRepInc() / (double) nUnqChar)
								:
								result.getInputs().getControls().getNRepInc()
						)
				);
			}
		}
	}

	private void checkAndRankSequentials(String typedTxt, Result result, Options options) {
		for (int s = 0; s <= ALPHAS.length() - options.getMultSeqAlpha(); s++) {
			String sFwd = limitedSubstr(ALPHAS, s, options.getMultSeqAlpha());
			String sRev = new StringBuilder(sFwd).reverse().toString();
			if (typedTxt.toLowerCase().contains(sFwd) || typedTxt.toLowerCase().contains(sRev)) {
				result.getInputs().getDeductions().setSequentialLetters(result.getInputs().getDeductions().getSequentialLetters() + 1);
				result.getInputs().getControls().setNSeqChar(result.getInputs().getControls().getNSeqChar() + 1);
			}
		}

		for (int s = 0; s <= NUMERICS.length() - options.getMultSeqNumber(); s++) {
			String sFwd = limitedSubstr(NUMERICS, s, options.getMultSeqNumber());
			String sRev = new StringBuilder(sFwd).reverse().toString();
			if (typedTxt.toLowerCase().contains(sFwd) || typedTxt.toLowerCase().contains(sRev)) {
				result.getInputs().getDeductions().setSequentialNumbers(result.getInputs().getDeductions().getSequentialNumbers() + 1);
				result.getInputs().getControls().setNSeqChar(result.getInputs().getControls().getNSeqChar() + 1);
			}
		}

		for (int s = 0; s <= SYMBOLS.length() - options.getMultSymbol(); s++) {
			String sFwd = limitedSubstr(SYMBOLS, s, options.getMultSymbol());
			String sRev = new StringBuilder(sFwd).reverse().toString();
			if (typedTxt.toLowerCase().contains(sFwd) || typedTxt.toLowerCase().contains(sRev)) {
				result.getInputs().getDeductions().setSequentialSymbols(result.getInputs().getDeductions().getSequentialSymbols() + 1);
				result.getInputs().getControls().setNSeqChar(result.getInputs().getControls().getNSeqChar() + 1);
			}
		}
	}

	private String limitedSubstr(String context, int s, int mult) {
		return context.substring(s, s + mult);
	}

	private void modifyOverallScore(Result result, Options options) {
		Addictions addictions = result.getInputs().getAddictions();
		Deductions deductions = result.getInputs().getDeductions();

		if (addictions.getUpperLetters() > 0 && addictions.getUpperLetters() < addictions.getNumberOfChars()) {
			result.setScore(result.getScore() + ((addictions.getNumberOfChars() - addictions.getUpperLetters()) * 2));
		}
		if (addictions.getLowerLetters() > 0 && addictions.getLowerLetters() < addictions.getNumberOfChars()) {
			result.setScore(result.getScore() + ((addictions.getNumberOfChars() - addictions.getLowerLetters()) * 2));
		}
		if (addictions.getNumbers() > 0 && addictions.getNumbers() < addictions.getNumberOfChars()) {
			result.setScore(result.getScore() + (addictions.getNumbers() * options.getMultNumber()));
		}
		if (addictions.getSymbols() > 0) {
			result.setScore(result.getScore() + (addictions.getSymbols() * options.getMultSymbol()));
		}
		if (addictions.getMiddleNumbersOrSymbols() > 0) {
			result.setScore(result.getScore() + (addictions.getMiddleNumbersOrSymbols() * options.getMultMidChar()));
		}

		if ((addictions.getLowerLetters() > 0 || addictions.getUpperLetters() > 0) && addictions.getSymbols() == 0 && addictions.getNumbers() == 0) {
			result.setScore(result.getScore() - addictions.getNumberOfChars());
			deductions.setLettersOnly(addictions.getNumberOfChars());
		}
		if (addictions.getLowerLetters() == 0 && addictions.getUpperLetters() == 0 && addictions.getSymbols() == 0 && addictions.getNumbers() > 0) {
			result.setScore(result.getScore() - addictions.getNumberOfChars());
			deductions.setNumbersOnly(addictions.getNumberOfChars());
		}

		if (deductions.getRepeatChars() > 0) {
			result.setScore(result.getScore() - result.getInputs().getControls().getNRepInc());
		}

		if (deductions.getConsecutiveUpper() > 0) {
			result.setScore(result.getScore() - (deductions.getConsecutiveUpper() * options.getMultConsecAlphaUC()));
		}
		if (deductions.getConsecutiveLower() > 0) {
			result.setScore(result.getScore() - (deductions.getConsecutiveLower() * options.getMultConsecAlphaLC()));
		}
		if (deductions.getConsecutiveNumbers() > 0) {
			result.setScore(result.getScore() - (deductions.getConsecutiveNumbers() * options.getMultConsecNumber()));
		}

		if (deductions.getSequentialLetters() > 0) {
			result.setScore(result.getScore() - (deductions.getSequentialLetters() * options.getMultSeqAlpha()));
		}
		if (deductions.getSequentialNumbers() > 0) {
			result.setScore(result.getScore() - (deductions.getSequentialNumbers() * options.getMultSeqNumber()));
		}
		if (deductions.getSequentialSymbols() > 0) {
			result.setScore(result.getScore() - (deductions.getSequentialSymbols() * options.getMultSeqSymbol()));
		}
	}

	private void calculateFinalScoreAndComplexity(Result result) {
		int nScore = result.getScore();
		if (nScore > 100) {
			nScore = 100;
		} else if (nScore < 0) {
			nScore = 0;
		}
		result.setScore(nScore);
		result.setComplexity(PasswordComplexityEnum.getByScore(result.getScore()));
	}

	private void calculateRequirements(String typedText, Result result, Options options) {
		Addictions addictions = result.getInputs().getAddictions();

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
				result.getInputs().getControls().setNReqChar(result.getInputs().getControls().getNReqChar() + 1);
			} else if (arrChars[c] > (minVal + 1)) {
				result.getInputs().getControls().setNReqChar(result.getInputs().getControls().getNReqChar() + 1);
			}
		}
		addictions.setRequirements(result.getInputs().getControls().getNReqChar());

		if (addictions.getRequirements() > nMinReqChars) {
			result.setScore(result.getScore() + (addictions.getRequirements() * 2));
		}
	}
}
