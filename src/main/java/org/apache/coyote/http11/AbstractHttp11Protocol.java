package org.apache.coyote.http11;

import org.apache.coyote.AbstractProtocol;
import org.apache.tomcat.util.net.AbstractEndpoint;

public abstract class AbstractHttp11Protocol<S> extends AbstractProtocol<S> {
    public AbstractHttp11Protocol(AbstractEndpoint<S, ?> endpoint) {
        super(endpoint);
    }

    public void setMaxHttpRequestHeaderSize(int valueI) {
//        maxHttpRequestHeaderSize = valueI;
        throw new UnsupportedOperationException();
    }

    public void setMaxHttpResponseHeaderSize(int valueI) {
//        maxHttpResponseHeaderSize = valueI;
        throw new UnsupportedOperationException();
    }

    public void setMaxSwallowSize(int maxSwallowSize) {
//        this.maxSwallowSize = maxSwallowSize;
        throw new UnsupportedOperationException();
    }
    public void setMaxKeepAliveRequests(int mkar) {
//        getEndpoint().setMaxKeepAliveRequests(mkar);
        throw new UnsupportedOperationException();
    }
}
