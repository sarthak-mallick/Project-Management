package com.projectmanagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projectmanagement.dao.UserDao;
import com.projectmanagement.models.User;
import com.projectmanagement.validator.NewUserValidator;
import com.projectmanagement.validator.UserValidator;
import com.projectmanagement.util.FormFlash;
import com.projectmanagement.util.PasswordUtil;

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
	public String newUser(ModelMap map) {
		if (!map.containsAttribute("user")) {
			map.addAttribute("user", new User());
		}
		return "new-user";
	}

	@PostMapping("/new-user")
	public String handleForm(@ModelAttribute User user, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {

		newUserValidator.validate(user, bindingResult);
        if(bindingResult.hasErrors()){
			user.setPassword(null);
			FormFlash.flashErrors(redirectAttributes, "user", user, bindingResult);
			return "redirect:/new-user";
        }
		user.setPassword(PasswordUtil.hash(user.getPassword()));
		userDao.saveUser(user);
		return "redirect:/user-added";
	}
	
	@GetMapping("/user-added")
	public String userAdded() {
		return "user-added";
	}
	
	@GetMapping("/login")
	public String login(ModelMap map) {
		if (!map.containsAttribute("user")) {
			map.addAttribute("user", new User());
		}
		return "login";
	}

	@PostMapping("/login")
	public String allProjects(@ModelAttribute User user, BindingResult bindingResult, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {

		userValidator.validate(user, bindingResult);
        if(!bindingResult.hasErrors()){
			int userId = userDao.authenticate(user);
			if (userId > 0) {
				request.getSession().setAttribute("userId", userId);
				return "redirect:/all-projects";
			}
			// Clear before rejecting: rejectValue snapshots the field value as the
			// rejected value, and the raw password must not reach the flash map or
			// the re-rendered input.
			user.setPassword(null);
			bindingResult.rejectValue("password", "invalid-credentials", "Invalid email or password");
        }
		else {
			user.setPassword(null);
		}
		FormFlash.flashErrors(redirectAttributes, "user", user, bindingResult);
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
