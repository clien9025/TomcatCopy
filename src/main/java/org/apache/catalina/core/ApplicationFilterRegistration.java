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
public class ApplicationFilterRegistration implements FilterRegistration.Dynamic{
    @Override
    public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames) {

    }

    @Override
    public Collection<String> getServletNameMappings() {
        return null;
    }

    @Override
    public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {

    }

    @Override
    public Collection<String> getUrlPatternMappings() {
        return null;
    }

    @Override
    public void setAsyncSupported(boolean isAsyncSupported) {
//        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getClassName() {
        return null;
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        return false;
    }

    @Override
    public String getInitParameter(String name) {
        return null;
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
