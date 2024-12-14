package com.projectmanagement.models;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Component
@Entity
@Table(name="tasks")
public class Task {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "assignee_id", unique = false)
    private User assignee;
        
    @ManyToOne
    @JoinColumn(name = "project_id", unique = false)
    private Project project;
    
    private double effortEstimate;
    private double effortLogged;
    
    @Transient
    private double percentComplete;
    
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getAssignee() {
		return assignee;
	}

	public void setAssignee(User assignee) {
		this.assignee = assignee;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public double getEffortEstimate() {
		return effortEstimate;
	}

	public void setEffortEstimate(double effortEstimate) {
		this.effortEstimate = effortEstimate;
	}

	public double getEffortLogged() {
		return effortLogged;
	}

	public void setEffortLogged(double effortLogged) {
		this.effortLogged = effortLogged;
	}
	
    public double getPercentComplete() {
    	if (effortEstimate > 0.0) 
    		return Math.round(100.0*effortLogged/effortEstimate);
    	return 0.0;
    }

    public void setPercentComplete(double percentComplete) {
        this.percentComplete = percentComplete;
    }

	@Override
	public String toString() {
		return "Task [id=" + id + ", name=" + name + ", assignee=" + assignee + ", project=" + project
				+ ", effortEstimate=" + effortEstimate + ", effortLogged=" + effortLogged + "]";
	}
	
}
