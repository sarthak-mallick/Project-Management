package com.projectmanagement.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class CommonValidator {
    public void regexValidate(String regEx, String fieldValue, Errors errors, String fieldName, String message) {
        Pattern pattern = Pattern.compile(regEx);
        Matcher m = pattern.matcher(fieldValue);
        if(!m.matches()){
            errors.rejectValue(fieldName, "invalid-"+fieldName, message);
        }
    }
}
