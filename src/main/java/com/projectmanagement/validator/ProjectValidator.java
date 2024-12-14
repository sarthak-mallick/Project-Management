package com.projectmanagement.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.projectmanagement.models.Project;

@Component
public class ProjectValidator implements Validator {
	@Autowired
	CommonValidator commonValidator;
	
    public boolean supports(Class<?> type) {
        return Project.class.isAssignableFrom(type);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Project project = (Project) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "empty-name", "Name cannot be empty");
        String nameRegex = "^[a-zA-Z0-9]+$";
        commonValidator.regexValidate(nameRegex, project.getName(), errors, "name", "Name is not valid. Enter a valid name");
    }

}
