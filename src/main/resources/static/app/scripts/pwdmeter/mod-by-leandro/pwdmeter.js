/*
 *   ############################################################################################
 *   WARNING!! IT'S NOT IN USE IN THIS PROJECT!  Please read the file 'readme.md' in this folder.
 *   ############################################################################################
 *
 *   Based on code hosted at https://passwordmeter.com/js/pwdmeter.js
 *   Refactoring by Leandro Santana.
 *   Credits to Jeff Todnem (http://www.todnem.com/)
 *   Copyright (C) 2007 Jeff Todnem
 *
 */

const ALPHAS = 'abcdefghijklmnopqrstuvwxyz';
const NUMERICS = '01234567890';
const SYMBOLS = ')!@#$%^&*()';
const DEFAULT_OPTIONS = {
    minLen: 8,
    multSeqAlpha: 3,
    multSeqNumber: 3,
    multSeqSymbol: 3,
    multLength: 4,
    multNumber: 4,
    multSymbol: 6,
    multMidChar: 2,
    multConsecAlphaUC: 2,
    multConsecAlphaLC: 2,
    multConsecNumber: 2
};

function checkPassword(typedTxt, options = DEFAULT_OPTIONS) {

    let result = null;

    if(typedTxt){
        result = initResult(typedTxt, options);

        checkAndRankSymbolsNumericLowerAndUper(typedTxt, result);

        checkAndRankSequentials(typedTxt, result, options);

        modifyOverallScore(result, options);

        calculateRequirements(typedTxt, result, options);

        calculateFinalScoreAndCompexity(result);
    }
    return result;
}

function initResult(typedText, options) {
    return {
        score: typedText.length * options.multLength,
        complexity: 0,
        inputs: {
            addictions: {
                numberOfChars: typedText.length,
                upperLetters: 0,
                lowerLetters: 0,
                numbers: 0,
                symbols: 0,
                middleNumbersOrSymbols: 0,
                requirements: 0
            },
            deductions: {
                lettersOnly: 0,
                numbersOnly: 0,
                repeatChars: 0,
                consecutiveUpper: 0,
                consecutiveLower: 0,
                consecutiveNumbers: 0,
                sequentialLetters: 0,
                sequentialNumbers: 0,
                sequentialSymbols: 0
            },
            controls: {
                nRepInc: 0,
                nSeqChar: 0,
                nReqChar: 0
            }
        }
    }
}

function checkAndRankSymbolsNumericLowerAndUper(typedTxt, result) {
    let arrPwd = typedTxt.replace(/\s+/g,'').split(/\s*/);
    let arrPwdLen = arrPwd.length;
    let nTmpAlphaUC, nTmpAlphaLC, nTmpNumber;

    /* Loop through password to check for Symbol, Numeric, Lowercase and Uppercase pattern matches */
    for (let a = 0; a < arrPwdLen; a++) {
        if (arrPwd[a].match(/[A-Z]/g)) {
            if (nTmpAlphaUC !== undefined) { if (((nTmpAlphaUC || 0) + 1) === a) { result.inputs.deductions.consecutiveUpper++;} }
            nTmpAlphaUC = a;
            result.inputs.addictions.upperLetters++;
        }
        else if (arrPwd[a].match(/[a-z]/g)) {
            if (nTmpAlphaLC !== undefined) { if (((nTmpAlphaLC || 0) + 1) === a) { result.inputs.deductions.consecutiveLower++; } }
            nTmpAlphaLC = a;
            result.inputs.addictions.lowerLetters++;
        }
        else if (arrPwd[a].match(/[0-9]/g)) {
            if (a > 0 && a < (arrPwdLen - 1)) {
                result.inputs.addictions.middleNumbersOrSymbols++;
            }
            if (nTmpNumber !== undefined) { if (((nTmpNumber || 0) + 1) === a) { result.inputs.deductions.consecutiveNumbers++;} }
            nTmpNumber = a;
            result.inputs.addictions.numbers++;
        }
        else if (arrPwd[a].match(/[^a-zA-Z0-9_]/g)) {
            if (a > 0 && a < (arrPwdLen - 1)) { result.inputs.addictions.middleNumbersOrSymbols++; }
            result.inputs.addictions.symbols++;
        }
        // Internal loop through password to check for repeat characters
        let bCharExists = false;
        for (let b=0; b < arrPwdLen; b++) {
            if (arrPwd[a] === arrPwd[b] && a !== b) {  // repeat character exists
                bCharExists = true;
                /*
                Calculate icrement deduction based on proximity to identical characters
                Deduction is incremented each time a new match is discovered
                Deduction amount is based on total password length divided by the
                difference of distance between currently selected match
                */
                result.inputs.controls.nRepInc += Math.abs(arrPwdLen/(b-a));
            }
        }
        if (bCharExists) {
            result.inputs.deductions.repeatChars++;
            let nUnqChar = arrPwdLen - result.inputs.deductions.repeatChars;
            result.inputs.controls.nRepInc = (nUnqChar) ? Math.ceil(result.inputs.controls.nRepInc/nUnqChar) : Math.ceil(result.inputs.controls.nRepInc);
        }
    }
}

function checkAndRankSequentials(typedTxt, result, options) {
    // Check for sequential alpha string patterns (forward and reverse)
    for (let s = 0; s < 23; s++) {
        const sFwd = ALPHAS.substring(s, (s + options.multSeqAlpha));
        const sRev = sFwd.strReverse();
        if (typedTxt.toLowerCase().indexOf(sFwd) !== -1 || typedTxt.toLowerCase().indexOf(sRev) !== -1) {
            result.inputs.deductions.sequentialLetters++;
            result.inputs.controls.nSeqChar++;
        }
    }

    // Check for sequential numeric string patterns (forward and reverse)
    for (let s = 0; s < 8; s++) {
        const sFwd = NUMERICS.substring(s, (s + options.multSeqNumber));
        const sRev = sFwd.strReverse();
        if (typedTxt.toLowerCase().indexOf(sFwd) !== -1 || typedTxt.toLowerCase().indexOf(sRev) !== -1) {
            result.inputs.deductions.sequentialNumbers++;
            result.inputs.controls.nSeqChar++;
        }
    }

    // Check for sequential symbol string patterns (forward and reverse)
    for (let s = 0; s < 8; s++) {
        const sFwd = SYMBOLS.substring(s, (s + options.multSymbol));
        const sRev = sFwd.strReverse();
        if (typedTxt.toLowerCase().indexOf(sFwd) !== -1 || typedTxt.toLowerCase().indexOf(sRev) !== -1) {
            result.inputs.deductions.sequentialSymbols++;
            result.inputs.controls.nSeqChar++;
        }
    }
}

function modifyOverallScore(result, options) {
    const addictions = result.inputs.addictions;
    const deductions = result.inputs.deductions;

    // General point assignment
    if (addictions.upperLetters > 0 && addictions.upperLetters < addictions.numberOfChars) {
        result.score = result.score + ((addictions.numberOfChars - addictions.upperLetters) * 2);
    }
    if (addictions.lowerLetters > 0 && addictions.lowerLetters < addictions.numberOfChars) {
        result.score = (result.score + ((addictions.numberOfChars - addictions.lowerLetters) * 2));
    }
    if (addictions.numbers > 0 && addictions.numbers < addictions.numberOfChars) {
        result.score = (result.score + (addictions.numbers * options.multNumber));
    }
    if (addictions.symbols > 0) {
        result.score = (result.score + (addictions.symbols * options.multSymbol));
    }
    if (addictions.middleNumbersOrSymbols > 0) {
        result.score = (result.score + (addictions.middleNumbersOrSymbols * options.multMidChar));
    }

    // Only Letters
    if ((addictions.lowerLetters > 0 || addictions.upperLetters > 0) && addictions.symbols === 0 && addictions.numbers === 0) {
        result.score = (result.score - addictions.numberOfChars);
        deductions.lettersOnly = addictions.numberOfChars;
    }
    // Only Numbers
    if (addictions.lowerLetters === 0 && addictions.upperLetters === 0 && addictions.symbols === 0 && addictions.numbers > 0) {
        result.score = (result.score - addictions.numberOfChars);
        deductions.numbersOnly = addictions.numberOfChars;
    }
    // Same character exists more than once
    if (deductions.repeatChars > 0) {
        result.score = (result.score - result.inputs.controls.nRepInc);
    }
    // Consecutive ones
    if (deductions.consecutiveUpper > 0) {
        result.score = (result.score - (deductions.consecutiveUpper * options.multConsecAlphaUC));
    }
    if (deductions.consecutiveLower > 0) {
        result.score = (result.score - (deductions.consecutiveLower * options.multConsecAlphaLC));
    }
    if (deductions.consecutiveNumbers > 0) {
        result.score = (result.score - (deductions.consecutiveNumbers * options.multConsecNumber));
    }
    // Sequential ones
    if (deductions.sequentialLetters > 0) {
        result.score = (result.score - (deductions.sequentialLetters * options.multSeqAlpha));
    }
    if (deductions.sequentialNumbers > 0) {
        result.score = (result.score - (deductions.sequentialNumbers * options.multSeqNumber));
    }
    if (deductions.sequentialSymbols > 0) {
        result.score = (result.score - (deductions.sequentialSymbols * options.multSeqSymbol));
    }
}

function calculateFinalScoreAndCompexity(result) {
    let nScore = result.score;
    let complexity = 'Too Short';
    if (nScore > 100) {
        nScore = 100;
    } else if (nScore < 0) {
        nScore = 0;
    }
    if (nScore >= 0 && nScore < 20) {
        complexity = 'Very Weak';
    } else if (nScore >= 20 && nScore < 40) {
        complexity = 'Weak';
    } else if (nScore >= 40 && nScore < 60) {
        complexity = 'Good';
    } else if (nScore >= 60 && nScore < 80) {
        complexity = 'Strong';
    } else if (nScore >= 80 && nScore <= 100) {
        complexity = 'Very Strong';
    }
    result.score = nScore;
    result.complexity =  complexity;
}

function calculateRequirements(typedText, result, options) {
    const addictions = result.inputs.addictions;

    // Determine if mandatory requirements have been met
    let arrChars = [addictions.numberOfChars,
        addictions.upperLetters,
        addictions.lowerLetters,
        addictions.numbers,
        addictions.symbols
    ];
    let arrCharsIds = ['numberOfChars', 'upperLetters', 'lowerLetters', 'numbers', 'symbols'];
    let arrCharsLen = arrChars.length;
    for (let c = 0; c < arrCharsLen; c++) {
        let minVal;
        if (arrCharsIds[c] === 'numberOfChars') {
            minVal = options.minLen - 1;
        } else {
            minVal = 0;
        }
        if (arrChars[c] === minVal + 1) {
            result.inputs.controls.nReqChar++;
        } else if (arrChars[c] > (minVal + 1)) {
            result.inputs.controls.nReqChar++;
        }
    }
    addictions.requirements = result.inputs.controls.nReqChar;

    let nMinReqChars;
    if (typedText.length >= options.minLen) {
        nMinReqChars = 3;
    } else {
        nMinReqChars = 4;
    }
    if (addictions.requirements > nMinReqChars) {  // One or more required characters exist
        result.score = (result.score + (addictions.requirements * 2));
    }
}

String.prototype.strReverse = function () {
    let newstring = '';
    for (let s = 0; s < this.length; s++) {
        newstring = this.charAt(s) + newstring;
    }
    return newstring;
};

