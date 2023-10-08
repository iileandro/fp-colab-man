const sAlphas = 'abcdefghijklmnopqrstuvwxyz';
const sNumerics = '01234567890';
const sSymbols = ')!@#$%^&*()';

// multiplicadores...
const nMultSeqAlpha = 3;
const nMultSeqNumber = 3;
const nMultSeqSymbol = 3;
const nMultLength = 4;
const nMultNumber = 4;
const nMultSymbol = 6;
const nMultMidChar=2;
const nMultConsecAlphaUC=2;
const nMultConsecAlphaLC=2;
const nMultConsecNumber=2;


function chkPass(pwd, minLen = 8) {
    if(!pwd) {
        return null;
    }
    
    let result = {
        score: pwd.length * nMultLength,
        complexity: 0,
        inputs: {
            addictions: {
                numberOfChars: pwd.length,
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
        },
        addictions: {
            numberOfChars: 0,
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
    }

    let arrPwd = pwd.replace(/\s+/g,'').split(/\s*/);
    let arrPwdLen = arrPwd.length;

    // temp vars
    let nTmpAlphaUC, nTmpAlphaLC, nTmpNumber, nTmpSymbol;
    
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
            nTmpSymbol = a;
            result.inputs.addictions.symbols++;
        }
        /* Internal loop through password to check for repeat characters */
        let bCharExists = false;
        for (let b=0; b < arrPwdLen; b++) {
            if (arrPwd[a] === arrPwd[b] && a !== b) { /* repeat character exists */
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

    /* Check for sequential alpha string patterns (forward and reverse) */
    for (let s = 0; s < 23; s++) {
        const sFwd = sAlphas.substring(s,(s + 3));
        const sRev = sFwd.strReverse();
        if (pwd.toLowerCase().indexOf(sFwd) !== -1 || pwd.toLowerCase().indexOf(sRev) !== -1) { result.inputs.deductions.sequentialLetters++; result.inputs.controls.nSeqChar++;}
    }

    /* Check for sequential numeric string patterns (forward and reverse) */
    for (let s = 0; s < 8; s++) {
        const sFwd = sNumerics.substring(s,(s + 3));
        const sRev = sFwd.strReverse();
        if (pwd.toLowerCase().indexOf(sFwd) !== -1 || pwd.toLowerCase().indexOf(sRev) !== -1) { result.inputs.deductions.sequentialNumbers++; result.inputs.controls.nSeqChar++;}
    }

    /* Check for sequential symbol string patterns (forward and reverse) */
    for (let s = 0; s < 8; s++) {
        const sFwd = sSymbols.substring(s,parseInt(String(s+3)));
        const sRev = sFwd.strReverse();
        if (pwd.toLowerCase().indexOf(sFwd) !== -1 || pwd.toLowerCase().indexOf(sRev) !== -1) { result.inputs.deductions.sequentialSymbols++; result.inputs.controls.nSeqChar++;}
    }

    /* Modify overall score value based on usage vs requirements */

    /* General point assignment */
    if (result.inputs.addictions.upperLetters > 0 && result.inputs.addictions.upperLetters < result.inputs.addictions.numberOfChars) {
        result.score = result.score + ((result.inputs.addictions.numberOfChars - result.inputs.addictions.upperLetters) * 2);
    }
    if (result.inputs.addictions.lowerLetters > 0 && result.inputs.addictions.lowerLetters < result.inputs.addictions.numberOfChars) {
        result.score = (result.score + ((result.inputs.addictions.numberOfChars - result.inputs.addictions.lowerLetters) * 2));
    }
    if (result.inputs.addictions.numbers > 0 && result.inputs.addictions.numbers < result.inputs.addictions.numberOfChars) {
        result.score = (result.score + (result.inputs.addictions.numbers * nMultNumber));
    }
    if (result.inputs.addictions.symbols > 0) {
        result.score = (result.score + (result.inputs.addictions.symbols * nMultSymbol));
    }
    if (result.inputs.addictions.middleNumbersOrSymbols > 0) {
        result.score = (result.score + (result.inputs.addictions.middleNumbersOrSymbols * nMultMidChar));
    }

    /* Point deductions for poor practices */
    if ((result.inputs.addictions.lowerLetters > 0 || result.inputs.addictions.upperLetters > 0)
        && result.inputs.addictions.symbols === 0 && result.inputs.addictions.numbers === 0) {  // Only Letters
        result.score = (result.score - result.inputs.addictions.numberOfChars);
        result.inputs.deductions.lettersOnly = result.inputs.addictions.numberOfChars;
    }
    /* Point deductions for poor practices */
    if ((result.inputs.addictions.lowerLetters > 0 || result.inputs.addictions.upperLetters > 0)
        && result.inputs.addictions.symbols === 0 && result.inputs.addictions.numbers === 0) {  // Only Letters
        result.score = (result.score - result.inputs.addictions.numberOfChars);
        result.inputs.deductions.lettersOnly = result.inputs.addictions.numberOfChars;
    }
    if (result.inputs.addictions.lowerLetters === 0 && result.inputs.addictions.upperLetters === 0
        && result.inputs.addictions.symbols === 0 && result.inputs.addictions.numbers > 0) {  // Only Numbers
        result.score = (result.score - result.inputs.addictions.numberOfChars);
        result.inputs.deductions.numbersOnly = result.inputs.addictions.numberOfChars;
    }

    // Same character exists more than once
    if (result.inputs.deductions.repeatChars > 0) {
        result.score = (result.score - result.inputs.controls.nRepInc);
    }
    if (result.inputs.deductions.consecutiveUpper > 0) {
        result.score = (result.score - (result.inputs.deductions.consecutiveUpper * nMultConsecAlphaUC));
    }
    if (result.inputs.deductions.consecutiveLower > 0) {
        result.score = (result.score - (result.inputs.deductions.consecutiveLower * nMultConsecAlphaLC));
    }
    if (result.inputs.deductions.consecutiveNumbers > 0) {
        result.score = (result.score - (result.inputs.deductions.consecutiveNumbers * nMultConsecNumber));
    }
    if (result.inputs.deductions.sequentialLetters > 0) {   // (3 characters or more)
        result.score = (result.score - (result.inputs.deductions.sequentialLetters * nMultSeqAlpha));
    }
    if (result.inputs.deductions.sequentialNumbers > 0) {  // (3 characters or more)
        result.score = (result.score - (result.inputs.deductions.sequentialNumbers * nMultSeqNumber));
    }
    if (result.inputs.deductions.sequentialSymbols > 0) {  // 3 characters or more
        result.score = (result.score - (result.inputs.deductions.sequentialSymbols * nMultSeqSymbol));
    }

    /* Determine if mandatory requirements have been met and set image indicators accordingly */
    let arrChars = [result.inputs.addictions.numberOfChars,
                    result.inputs.addictions.upperLetters,
                    result.inputs.addictions.lowerLetters,
                    result.inputs.addictions.numbers,
                    result.inputs.addictions.symbols
    ];
    let arrCharsIds = ['numberOfChars', 'upperLetters', 'lowerLetters', 'numbers', 'symbols'];
    let arrCharsLen = arrChars.length;
    for (let c = 0; c < arrCharsLen; c++) {
        let minVal;
        if (arrCharsIds[c] === 'numberOfChars') {
            minVal = minLen - 1;
        } else {
            minVal = 0;
        }
        if (arrChars[c] === minVal + 1) {
            result.inputs.controls.nReqChar++;
        } else if (arrChars[c] > (minVal + 1)) {
            result.inputs.controls.nReqChar++;
        }
    }
    result.inputs.addictions.requirements = result.inputs.controls.nReqChar;
    
    let nMinReqChars;
    if (pwd.length >= minLen) {
        nMinReqChars = 3;
    } else {
        nMinReqChars = 4;
    }
    if (result.inputs.addictions.requirements > nMinReqChars) {  // One or more required characters exist
        result.score = (result.score + (result.inputs.addictions.requirements * 2));
    }

    result.complexity = getComplexityByScore(result.score);

    return result;
}

String.prototype.strReverse = function () {
    let newstring = '';
    for (let s = 0; s < this.length; s++) {
        newstring = this.charAt(s) + newstring;
    }
    return newstring;
};

function getComplexityByScore(nScore) {
    let sComplexity = 'Too Short';
    if (nScore > 100) {
        nScore = 100;
    } else if (nScore < 0) {
        nScore = 0;
    }
    if (nScore >= 0 && nScore < 20) {
        sComplexity = 'Very Weak';
    } else if (nScore >= 20 && nScore < 40) {
        sComplexity = 'Weak';
    } else if (nScore >= 40 && nScore < 60) {
        sComplexity = 'Good';
    } else if (nScore >= 60 && nScore < 80) {
        sComplexity = 'Strong';
    } else if (nScore >= 80 && nScore <= 100) {
        sComplexity = 'Very Strong';
    }
    return sComplexity;
}
