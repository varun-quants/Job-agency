package com.agency.util;

import java.util.List;

/**Every piece of data entered by a user passes through Validator before reaching the service layer. */
//It is important to make sure that invalid data must never reach the repository or file.
public class Validator {

    /**Method for validating if input entered is empty.*/
    public static void validateNotEmpty(String value, String fieldName){
        if(value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
    }

    /**Method for validating if input entered is zero or negative. */
    public static void validatePositive(double value, String fieldName) {
        if(value == 0 || value < 0 ){
            throw new IllegalArgumentException(fieldName + " cannot be zero or negative.");
        }
    }

    /**Method for validating if email input entered is not null, empty, always contains an '@' symbol and a '.'(FULLSTOP) */
    public static void validateEmail(String email){
        if(email == null || email.trim().isEmpty() || !email.contains("@") || !email.contains(".") ){
            throw new IllegalArgumentException("Invalid email address.");
        }
    }

    /**Method for validating if phone input entered satisfies constraints. */
    public static void validatePhone(String phone){
        if(phone == null || phone.contains(" ") || phone.isEmpty()){
            throw new IllegalArgumentException("Phone cannot be empty.");
        }

        if(phone.length() <10 || phone.length() >15){
            throw new IllegalArgumentException("Phone length must be between 10 and 15 digits. ");
        }

        if(!phone.matches("\\d+")) {
            throw new IllegalArgumentException("Phone must contain only digits. ");
        }
    }

    /**Method for checking that the skills are not empty*/
    public static void validateSkillListNotEmpty(List<Integer> skillIds){
        if(skillIds == null || skillIds.isEmpty()){
            throw new IllegalArgumentException("Skill list must not be empty.");
        }
    }

}
