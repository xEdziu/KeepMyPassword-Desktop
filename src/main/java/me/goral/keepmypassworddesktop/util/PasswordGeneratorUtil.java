package me.goral.keepmypassworddesktop.util;

import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import static org.passay.IllegalCharacterRule.ERROR_CODE;

public class PasswordGeneratorUtil {

    public static String generatePassword(int length, int lowerNum, int upperNum, int digitNum, int specialNum){
        PasswordGenerator gen = new PasswordGenerator();

        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(lowerNum);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(upperNum);

        CharacterData digitCaseChars = EnglishCharacterData.UpperCase;
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
    }
}
