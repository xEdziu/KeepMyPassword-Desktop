package me.goral.keepmypassworddesktop.util;

import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import static org.passay.IllegalCharacterRule.ERROR_CODE;

public class PasswordGeneratorUtil {

    /**
     * Generate a password of a given length, with a given number of lowercase letters, uppercase
     * letters, digits, and special characters
     * 
     * @param length The length of the password to generate.
     * @param lowerNum The number of lower case characters to include in the password.
     * @param upperNum The number of upper case characters to include in the password.
     * @param digitNum The number of digits to include in the password.
     * @param specialNum The number of special characters to include in the password.
     * @return The generated password.
     */
    public static String generatePassword(int length, int lowerNum, int upperNum, int digitNum, int specialNum){

        if ((lowerNum + upperNum + digitNum + specialNum) > length){
            AlertsUtil.showErrorDialog("Error Dialog", "Invalid input data",
                    "Sum of all arguments is greater than general length");
            return null;
        } else if (length < 5) {
            AlertsUtil.showErrorDialog("Error Dialog", "Invalid input data",
                    "Length must be at least 5");
            return null;
        } else if (lowerNum < 1) {
            AlertsUtil.showErrorDialog("Error Dialog", "Invalid input data",
                    "Lower case number must not be lower than 1");
            return null;
        } else if (upperNum < 1) {
            AlertsUtil.showErrorDialog("Error Dialog", "Invalid input data",
                    "Upper case number must not be lower than 1");
            return null;
        } else if (digitNum < 1) {
            AlertsUtil.showErrorDialog("Error Dialog", "Invalid input data",
                    "Digit number must not be lower than 1");
            return null;
        } else if (specialNum < 1) {
            AlertsUtil.showErrorDialog("Error Dialog", "Invalid input data",
                    "Special character number must not be lower than 1");
            return null;
        } else {

            try {
                PasswordGenerator gen = new PasswordGenerator();

                CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
                CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
                lowerCaseRule.setNumberOfCharacters(lowerNum);

                CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
                CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
                upperCaseRule.setNumberOfCharacters(upperNum);

                CharacterData digitCaseChars = EnglishCharacterData.Digit;
                CharacterRule digitCaseRule = new CharacterRule(digitCaseChars);
                digitCaseRule.setNumberOfCharacters(digitNum);

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
                splCharRule.setNumberOfCharacters(specialNum);

                return gen.generatePassword(length, splCharRule, lowerCaseRule,
                        upperCaseRule, digitCaseRule);
            } catch (Exception e){
                AlertsUtil.showExceptionStackTraceDialog(e);
            }

            return null;
        }
    }
}
