package com.projectmanagement.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.projectmanagement.models.Project;
import com.projectmanagement.models.Task;
import com.projectmanagement.models.User;

@Repository
public class ProjectDao extends Dao<Project> {
	
    public void saveProject(Project project){
    	saveObject(project);
    }
    
    public void updateProject(Project project){
    	updateObject(project);
    }
    
    public void deleteProject(Project project, int userId){
    	if (project.getProjectManagerId() == userId)
    		deleteObject(project);
    }
    
    public Project getProjectById(int id) {
        Query<Project> q = getSession().createQuery("from Project where id= :id", Project.class);
        q.setParameter("id", id);
        return (Project) q.uniqueResult();
    }
    
    public List<Project> getAllProjectsByUserId(int userId) {
    	String hql = "select * from projects where id in ("+
    					"((select distinct id from projects where projectManagerId= :userId)"+
    					" union "+
    					"(select distinct project_id from tasks where assignee_id= :userId)))";
        Query<Project> q = getSession().createNativeQuery(hql, Project.class);
        q.setParameter("userId", userId);
        List<Project> res = q.list();
        return res;
    }
    
    public List<Task> getAllTasksByProjectId(int projectId) {
    	Project p = getProjectById(projectId);
        List<Task> res = p.getTasks();
        return res;
    }
    
    public List<User> getAllUsersByProjectId(int projectId) {
    	Project p = getProjectById(projectId);
        List<User> res = p.getTeamMembers();
        return res;
    }
	
}
