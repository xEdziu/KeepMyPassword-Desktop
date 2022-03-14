package me.goral.keepmypassworddesktop.util;

import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import static org.passay.IllegalCharacterRule.ERROR_CODE;

public class PasswordGeneratorUtil {

    public static String generatePassword(int length, int lowerNum, int upperNum, int digitNum, int specialNum){

        if ((lowerNum + upperNum + digitNum + specialNum) > length){
            AlertsUtil.showErrorDialog("Error Dialog", "Invalid input data",
                    "Sum of all arguments is greater than general length");
            return null;
        } else if (length < 1) {
            AlertsUtil.showErrorDialog("Error Dialog", "Invalid input data",
                    "Length must be at least 1");
            return null;
        } else if (lowerNum < 0) {
            AlertsUtil.showErrorDialog("Error Dialog", "Invalid input data",
                    "Lower case number must not be lower than 0");
            return null;
        } else if (upperNum < 0) {
            AlertsUtil.showErrorDialog("Error Dialog", "Invalid input data",
                    "Upper case number must not be lower than 0");
            return null;
        } else if (digitNum < 0) {
            AlertsUtil.showErrorDialog("Error Dialog", "Invalid input data",
                    "Digit number must not be lower than 0");
            return null;
        } else if (specialNum < 0) {
            AlertsUtil.showErrorDialog("Error Dialog", "Invalid input data",
                    "Special character number must not be lower than 0");
            return null;
        } else {

            PasswordGenerator gen = new PasswordGenerator();

            CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
            CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
            lowerCaseRule.setNumberOfCharacters(lowerNum == 0 ? 1 : lowerNum);

            CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
            CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
            upperCaseRule.setNumberOfCharacters(upperNum == 0 ? 1 : upperNum);

            CharacterData digitCaseChars = EnglishCharacterData.Digit;
            CharacterRule digitCaseRule = new CharacterRule(digitCaseChars);
            digitCaseRule.setNumberOfCharacters(digitNum == 0 ? 1 : digitNum);

            CharacterData specialChars = new CharacterData() {
                @Override
                public String getErrorCode() {
                    return ERROR_CODE;
                }

                @Override
                public String getCharacters() {
                    return "!@#$%^&*()_+";
                }
            };
            CharacterRule splCharRule = new CharacterRule(specialChars);
            splCharRule.setNumberOfCharacters(specialNum == 0 ? 1 : specialNum);

            return gen.generatePassword(length, splCharRule, lowerCaseRule,
                    upperCaseRule, digitCaseRule);
        }
    }
}
