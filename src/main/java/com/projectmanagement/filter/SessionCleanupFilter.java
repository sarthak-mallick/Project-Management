package com.projectmanagement.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.projectmanagement.dao.Dao;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * Closes the Hibernate session bound to the current (pooled) thread once the
 * request — including view rendering — has finished. Without this, the
 * thread-local session opened in {@link Dao#getSession()} is never released, so
 * its DB connection and first-level cache leak across requests on reused Tomcat
 * threads. Registered for all URLs, so the auth-excluded routes are covered too.
 */
@Component
public class SessionCleanupFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } finally {
            Dao.close();
        }
    }
}
