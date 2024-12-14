package com.projectmanagement.dao;

import java.util.ArrayList;

import org.hibernate.CacheMode;
import org.hibernate.HibernateException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.projectmanagement.models.Project;
import com.projectmanagement.models.Task;
import com.projectmanagement.models.User;

@Repository
public class TaskDao extends Dao<Task> {	
    public void saveTask(Task task){
    	saveObject(task);
    }
    
    public void updateTask(Task task){
    	updateObject(task);
    }
    
    public void deleteTask(Task task){
        deleteObject(task);
    }
    
    public Task getTaskById(int id) {
        Query<Task> q = getSession().createQuery("from Task where id= :id", Task.class);
        q.setParameter("id", id);
        return (Task) q.uniqueResult();
    }
    
    public void addTaskForProject(Project project, Task task, ProjectDao projectDao) {
    	Task t = new Task();
    	t.setName(task.getName());
        project.addTask(t);
        projectDao.updateProject(project);
    }
    
    public void removeTaskFromProject(Project project, Task task, ProjectDao projectDao) {
    	Task t = getTaskById(task.getId());
        project.removeTask(t);
        projectDao.updateProject(project);
    }
    
    public ArrayList<String> addTaskForUser(int taskId, int userId, UserDao userDao) {
        Task task = getTaskById(taskId);
        User user = userDao.getUserById(userId);
        user.addTask(task);
        userDao.updateUser(user);
        
        ArrayList<String> taskAndUser = new ArrayList<>();
        taskAndUser.add(task.getName());
        taskAndUser.add(user.getName());
        return taskAndUser;
    }
    
    public void saveTaskEdit(Task task, Task taskInDb, Project project, ProjectDao projectDao) {
    	taskInDb.setEffortEstimate(task.getEffortEstimate());
    	taskInDb.setEffortLogged(task.getEffortLogged());
    	updateTask(taskInDb);
    }
	
}
