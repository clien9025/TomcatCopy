package org.apache.coyote.http11;

import org.apache.coyote.AbstractProtocol;
import org.apache.tomcat.util.net.AbstractEndpoint;

public abstract class AbstractHttp11Protocol<S> extends AbstractProtocol<S> {
    public AbstractHttp11Protocol(AbstractEndpoint<S, ?> endpoint) {
        super(endpoint);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Over-ridden here to make the method visible to nested classes.
     */
    @Override
    protected AbstractEndpoint<S,?> getEndpoint() {
        return super.getEndpoint();
    }

    /**
     * Maximum size of the HTTP request message header.
     * HTTP 请求消息标头的最大大小
     */
    private int maxHttpRequestHeaderSize = -1;
    public void setMaxHttpRequestHeaderSize(int valueI) {
        maxHttpRequestHeaderSize = valueI;
    }

    /**
     * Maximum size of the HTTP response message header.
     * HTTP 响应消息标头的最大大小
     */
    private int maxHttpResponseHeaderSize = -1;

    public void setMaxHttpResponseHeaderSize(int valueI) {
        maxHttpResponseHeaderSize = valueI;
    }

    /**
     * Maximum amount of request body to swallow.
     * 最大可吞噬的请求体量。
     * maxSwallowSize的初始值设为2 * 1024 * 1024，这等于2MB（1024*1024是1MB）。
     */
    private int maxSwallowSize = 2 * 1024 * 1024;

    /**
     * setMaxSwallowSize 是用于限制可“吞噬”（或接受）的请求体的最大字节数。
     * setMaxSwallowSize方法允许用户设置自定义的最大吞噬大小。通过限制可吞噬的请求体大小，
     * 服务器可以更有效地管理资源，避免由于处理过大的请求体而导致的问题。
     * @param maxSwallowSize
     */
    public void setMaxSwallowSize(int maxSwallowSize) {
        this.maxSwallowSize = maxSwallowSize;
    }

    public void setMaxKeepAliveRequests(int mkar) {
        getEndpoint().setMaxKeepAliveRequests(mkar);
    }
}
