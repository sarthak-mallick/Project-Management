package com.projectmanagement.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;

import com.projectmanagement.dao.ProjectDao;
import com.projectmanagement.dao.TaskDao;
import com.projectmanagement.dao.UserDao;
import com.projectmanagement.models.Project;
import com.projectmanagement.models.Task;
import com.projectmanagement.models.User;
import com.projectmanagement.validator.TaskValidator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class TaskController {
	
	@Autowired
	TaskValidator taskValidator;
	
	@GetMapping("/all-tasks")
	public String allTasks(@RequestParam("id") int projectId, ProjectDao projectDao, UserDao userDao, 
			HttpServletRequest request, ModelMap modelMap, Task task) {
		
		addModelsToAllProjects(projectDao, userDao, modelMap, projectId, task);
        return "all-tasks";
	}	
	
	@GetMapping("/new-task")
	public String showForm(ModelMap modelMap, Task task, @RequestParam("id") int projectId) {
		modelMap.addAttribute("task", task);
		modelMap.addAttribute("projectId", projectId);
		return "new-task";
	}
	
	@PostMapping("/new-task")
	public String handleForm(@ModelAttribute("task") Task task, @RequestParam("id") int projectId, BindingResult bindingResult, User assignee,
			SessionStatus status, TaskDao taskDao, ProjectDao projectDao, UserDao userDao, HttpServletRequest request, ModelMap modelMap) {
		
		taskValidator.validate(task, bindingResult);
        if(bindingResult.hasErrors()){
            return "new-task";
        }
		try {
			int userId = (int) request.getSession().getAttribute("userId");
			Project project = projectDao.getProjectById(projectId);
			taskDao.addTaskForProject(project, task, projectDao);
			status.setComplete();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		addModelsToAllProjects(projectDao, userDao, modelMap, projectId, task);
        return "redirect:/all-tasks?id="+projectId;	
    }


	@GetMapping("delete-task")
	public String deleteTask(@RequestParam("id") int taskId, @RequestParam("projectId") int projectId, SessionStatus status,
			TaskDao taskDao, ProjectDao projectDao, UserDao userDao, ModelMap modelMap, HttpServletRequest request) {
		
		int userId = (int) request.getSession().getAttribute("userId");
		Project project = projectDao.getProjectById(projectId);
		Task task = taskDao.getTaskById(taskId);
		try {
			taskDao.removeTaskFromProject(project, task, projectDao);
			status.setComplete();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		addModelsToAllProjects(projectDao, userDao, modelMap, projectId, task);
        return "redirect:/all-tasks?id="+projectId;	
    }
	
    @PostMapping("/updateTaskAssignee")
    @ResponseBody
    public void updateTaskAssignee(@RequestParam int taskId, @RequestParam int userId, TaskDao taskDao, UserDao userDao,
    		HttpServletResponse response) throws IOException {        
        
    	ArrayList<String> taskAndUser = taskDao.addTaskForUser(taskId, userId, userDao);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Task "+taskAndUser.get(0)+" is now assigned to "+taskAndUser.get(1));
    }
    
	@GetMapping("/edit-task")
	public String showTaskForm(@RequestParam("id") int taskId, @RequestParam("projectId") int projectId, TaskDao taskDao,
			HttpServletRequest request, ModelMap modelMap) {
		Task task = taskDao.getTaskById(taskId);
        modelMap.addAttribute("task", task);
        modelMap.addAttribute("projectId", projectId);
        return "edit-task";
	}	
	
	@PostMapping("/edit-task")
	public String editTaskForm(@ModelAttribute Task task, TaskDao taskDao, UserDao userDao, ProjectDao projectDao,
			HttpServletRequest request, ModelMap modelMap, SessionStatus status) {
    	
		int userId = (int) request.getSession().getAttribute("userId");
		Task taskInDb = taskDao.getTaskById(task.getId());
		Project project = taskInDb.getProject();
		int projectId = project.getId();
		try {
			taskDao.saveTaskEdit(task, taskInDb, project, projectDao);
			status.setComplete();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		addModelsToAllProjects(projectDao, userDao, modelMap, projectId, task);
        return "redirect:/all-tasks?id="+projectId;
	}
	
	private void addModelsToAllProjects(ProjectDao projectDao, UserDao userDao, ModelMap modelMap, int projectId, Task task) {
		List<Task> tasks = projectDao.getAllTasksByProjectId(projectId);
        modelMap.addAttribute("taskList", tasks);
        modelMap.addAttribute("projectId", projectId);
		modelMap.addAttribute("task", task);
		
		List<User> userList = userDao.getAllUsers();
		Map<Integer, List<User>> userMapForTask = new HashMap<>();
		for(Task t : tasks) {
			List<User> newUserList = new ArrayList<>(userList);
			if (t.getAssignee() != null) {
				newUserList.removeIf(u -> u.getId() == t.getAssignee().getId());
			}
			userMapForTask.put(t.getId(), newUserList);
		}
        modelMap.addAttribute("userMap", userMapForTask);
	}
}
