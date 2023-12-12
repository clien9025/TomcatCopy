package org.apache.tomcat.util.descriptor.web;

import java.io.Serializable;
import java.util.List;

public class ResourceBase implements Serializable, Injectable {
    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addInjectionTarget(String injectionTargetName, String jndiName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<InjectionTarget> getInjectionTargets() {
        throw new UnsupportedOperationException();
    }
}
