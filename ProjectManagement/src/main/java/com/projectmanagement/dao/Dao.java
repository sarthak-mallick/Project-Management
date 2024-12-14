package com.projectmanagement.dao;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import com.projectmanagement.models.Project;

public abstract class Dao<T> {
	@Autowired
    private static Configuration cfg = new Configuration();
    private static SessionFactory sessionFactory = cfg.configure("hibernate.cfg.xml").buildSessionFactory();
    private static final ThreadLocal sessionThread = new ThreadLocal();
    private static final Logger log = Logger.getAnonymousLogger();

    protected Dao(){}

    public static Session getSession(){
        Session session = (Session) Dao.sessionThread.get();

        if(session == null){
            session = sessionFactory.openSession();
            sessionThread.set(session);
        }
        return session;
    }

    protected void begin(){
        getSession().beginTransaction();
    }

    protected void commit(){
        getSession().getTransaction().commit();
    }

    protected void rollback(){
        try{
            getSession().getTransaction().rollback();
        } catch (HibernateException e){
            log.log(Level.WARNING, "Cannot Rollback",e);
        }
    }

    public static void close(){
        getSession().close();
    }
    
    public void saveObject(T object){
        try{
           begin();
           getSession().persist(object);
           commit();
        } catch (HibernateException e){
           rollback();
        }
    }
    
    public void updateObject(T object){
        try{
            begin();
            getSession().merge(object);
            commit();
        } catch (HibernateException e){
            rollback();
        }
    }
    
    public void deleteObject(T object){
        try{
            begin();
            getSession().remove(object);
            commit();
        } catch (HibernateException e){
            rollback();
        }
    }
}