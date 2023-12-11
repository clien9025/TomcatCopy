package org.apache.catalina.core;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.ServletSecurityElement;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author zhanyang
 */
public class ApplicationServletRegistration implements ServletRegistration.Dynamic{

    // todo: 调用了
    @Override
    public void setLoadOnStartup(int loadOnStartup) {

    }

    @Override
    public Set<String> setServletSecurity(ServletSecurityElement constraint) {
        return null;
    }

    // todo: 调用了
    @Override
    public void setMultipartConfig(MultipartConfigElement multipartConfig) {

    }

    @Override
    public void setRunAsRole(String roleName) {

    }

    @Override
    public void setAsyncSupported(boolean isAsyncSupported) {
//        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> addMapping(String... urlPatterns) {
        return null;
    }

    @Override
    public Collection<String> getMappings() {
        return null;
    }

    @Override
    public String getRunAsRole() {
        return null;
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
