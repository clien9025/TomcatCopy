package org.apache.catalina.core;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterRegistration;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * @author zhanyang
 */
public class ApplicationFilterRegistration implements FilterRegistration.Dynamic {
    @Override
    public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames) {

    }

    @Override
    public Collection<String> getServletNameMappings() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {
        System.out.println("public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {");
//        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> getUrlPatternMappings() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAsyncSupported(boolean isAsyncSupported) {
        System.out.println("public void setAsyncSupported(boolean isAsyncSupported) {");
//        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getInitParameter(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> setInitParameters(Map<String, String> initParameters) {
        return null;
    }

    @Override
    public Map<String, String> getInitParameters() {
        return null;
    }
}
