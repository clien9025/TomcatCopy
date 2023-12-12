package org.apache.catalina.core;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.ServletSecurityElement;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;

import java.util.*;

/**
 * @author zhanyang
 */
public class ApplicationServletRegistration implements ServletRegistration.Dynamic{

    private final Wrapper wrapper;
    private final Context context;
    public ApplicationServletRegistration(Wrapper wrapper, Context context) {
        this.wrapper = wrapper;
        this.context = context;

    }

    // todo: 调用了
    @Override
    public void setLoadOnStartup(int loadOnStartup) {
        System.out.println("public void setLoadOnStartup(int loadOnStartup) {");
//        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> setServletSecurity(ServletSecurityElement constraint) {
        throw new UnsupportedOperationException();
    }

    // todo: 调用了
    @Override
    public void setMultipartConfig(MultipartConfigElement multipartConfig) {
        System.out.println("public void setMultipartConfig(MultipartConfigElement multipartConfig) {");
//        throw new UnsupportedOperationException();
    }

    @Override
    public void setRunAsRole(String roleName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAsyncSupported(boolean asyncSupported) {
//        System.out.println("public void setAsyncSupported(boolean isAsyncSupported) {");
        wrapper.setAsyncSupported(asyncSupported);
    }

    @Override
    public Set<String> addMapping(String... urlPatterns) {
        // todo 自己实现的，用来站位
        return new HashSet<>(Arrays.asList(urlPatterns));
    }

    @Override
    public Collection<String> getMappings() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRunAsRole() {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String> getInitParameters() {
        throw new UnsupportedOperationException();
    }
}
