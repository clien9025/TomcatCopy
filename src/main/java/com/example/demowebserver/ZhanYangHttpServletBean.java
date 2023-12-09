package com.example.demowebserver;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;


public class ZhanYangHttpServletBean extends HttpServlet implements EnvironmentCapable, EnvironmentAware {

    @Override
    public final void init() throws ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setEnvironment(Environment environment) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Environment getEnvironment() {
        throw new UnsupportedOperationException();
    }
}
