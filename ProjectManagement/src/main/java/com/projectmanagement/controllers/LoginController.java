package com.projectmanagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.projectmanagement.dao.UserDao;
import com.projectmanagement.models.User;
import com.projectmanagement.validator.NewUserValidator;
import com.projectmanagement.validator.UserValidator;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LoginController {
	
	@Autowired
	UserDao userDao;
	
    @Autowired
    UserValidator userValidator;
    
    @Autowired
    NewUserValidator newUserValidator;
	

	@GetMapping("/new-user")
	public String newUser(User user, ModelMap map) {
		return "new-user";
	}
	
	@PostMapping("/new-user")
	public String handleForm(@ModelAttribute User user, ModelMap map, BindingResult bindingResult) {
		
		newUserValidator.validate(user, bindingResult);
        if(bindingResult.hasErrors()){
            return "new-user";
        }
		userDao.saveUser(user);
		return "redirect:/user-added";
	}
	
	@GetMapping("/user-added")
	public String userAdded() {
		return "user-added";
	}
	
	@GetMapping("/login")
	public String login(User user, ModelMap map) {
		return "login";
	}

	@PostMapping("/login")
	public String allProjects(@ModelAttribute User user, ModelMap map, HttpServletRequest request, BindingResult bindingResult) {
		
		userValidator.validate(user, bindingResult);
        if(bindingResult.hasErrors()){
            return "login";
        }
		int userId = userDao.authenticate(user);
		if (userId > 0) {
			request.getSession().setAttribute("userId", userId);
			return "redirect:/all-projects";
		}
		return "redirect:/login";
	}
	
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
    	
        if (request.getSession() != null) {
            request.getSession().invalidate();
        }
        return "logout";
    }
}
