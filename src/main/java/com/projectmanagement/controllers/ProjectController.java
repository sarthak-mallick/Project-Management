package com.projectmanagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projectmanagement.dao.ProjectDao;
import com.projectmanagement.dao.TaskDao;
import com.projectmanagement.dao.UserDao;
import com.projectmanagement.models.Project;
import com.projectmanagement.models.Task;
import com.projectmanagement.models.User;
import com.projectmanagement.util.FormFlash;
import com.projectmanagement.validator.ProjectValidator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ProjectController {
	@Autowired 
	ProjectValidator projectValidator;

	@GetMapping("/all-projects")
	public ModelAndView allProjects(ProjectDao projectDao, HttpServletRequest request) {
		int userId = (int) request.getSession().getAttribute("userId");
		return new ModelAndView("all-projects", "projectList", projectDao.getAllProjectsByUserId(userId));
	}
	
	@PostMapping("delete-project")
	public String deleteProject(@RequestParam("id") int projectId, SessionStatus status, ProjectDao projectDao,
			ModelMap modelMap, HttpServletRequest request) {
		
		int userId = (int) request.getSession().getAttribute("userId");
		Project project = projectDao.getProjectById(projectId);
		if (project != null)
			projectDao.deleteProject(project, userId);
		status.setComplete();
		return "redirect:/all-projects";
	}
	
	@GetMapping("/new-project")
	public String showForm(ModelMap modelMap) {
		if (!modelMap.containsAttribute("project")) {
			modelMap.addAttribute("project", new Project());
		}
		return "new-project";
	}

	@PostMapping("/new-project")
	public String handleForm(@ModelAttribute("project") Project project, BindingResult bindingResult,
			SessionStatus status, ProjectDao projectDao, UserDao userDao, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {

		projectValidator.validate(project, bindingResult);
        if(bindingResult.hasErrors()){
			FormFlash.flashErrors(redirectAttributes, "project", project, bindingResult);
			return "redirect:/new-project";
        }
		int userId = (int) request.getSession().getAttribute("userId");
		try {
			project.setProjectManagerId(userId);
			projectDao.saveProject(project);
			status.setComplete();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return "redirect:/all-projects";
	}
}
