package com.projectmanagement.dao;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.projectmanagement.models.Project;
import com.projectmanagement.models.User;

@Repository
public class UserDao extends Dao<User> {
	
    public void saveUser(User user){
    	saveObject(user);
    }
    
    public void updateUser(User user){
    	updateObject(user);
    }
    
    public int authenticate(User user) {
    	String email = user.getEmail();
    	String password = user.getPassword();
        Query<User> q = getSession().createQuery("from User where email= :email and password= :password", User.class);
        q.setParameter("email", email);
        q.setParameter("password", password);
        if (q.uniqueResult() != null) {
        	User u = (User) q.uniqueResult();
        	return u.getId();
        }
        return -1;
    }
    
    public User getUserById(int id) {
        Query<User> q = getSession().createQuery("from User where id= :id", User.class);
        q.setParameter("id", id);
        return (User) q.uniqueResult();
    }
    
    public User getUserByEmail(String email) {
        Query<User> q = getSession().createQuery("from User where email= :email", User.class);
        q.setParameter("email", email);
        return (User) q.uniqueResult();
    }

    public List<User> getAllUsers(){
        Query<User> q = getSession().createQuery("from User", User.class);
        List<User> res = q.list();
        return res;
    }
}
