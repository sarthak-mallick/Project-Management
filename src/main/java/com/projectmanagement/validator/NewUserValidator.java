package com.projectmanagement.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.projectmanagement.dao.UserDao;
import com.projectmanagement.models.User;

@Component
public class NewUserValidator extends UserValidator {
	@Autowired
	CommonValidator commonValidator;
	
	@Autowired
	UserDao userDao;

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "empty-name", "Name cannot be empty");
        String nameRegex = "[a-zA-Z ]*";
        commonValidator.regexValidate(nameRegex, user.getName(), errors, "name", "Name is not valid. Enter a valid name");
        
        User userInDb = userDao.getUserByEmail(user.getEmail());
        if (userInDb != null)
            errors.rejectValue("email", "invalid-email", "Account is already registered with this email");
        
        super.validate(target, errors);
    }
}
