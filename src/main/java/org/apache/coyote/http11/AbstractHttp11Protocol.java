package org.apache.coyote.http11;

import jakarta.servlet.http.HttpUpgradeHandler;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.Processor;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.UpgradeToken;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SocketWrapperBase;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractHttp11Protocol<S> extends AbstractProtocol<S> {
    public AbstractHttp11Protocol(AbstractEndpoint<S,?> endpoint) {
        super(endpoint);
        setConnectionTimeout(Constants.DEFAULT_CONNECTION_TIMEOUT);
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

    /**
     * 这行代码定义了一个私有布尔变量rejectIllegalHeader，并初始化为true。
     * 这意味着默认行为是拒绝包含非法头部的请求。
     */
    private boolean rejectIllegalHeader = true;

    /**
     * If an HTTP request is received that contains an illegal header name or value (e.g. the header name is not a
     * token) should the request be rejected (with a 400 response) or should the illegal header be ignored?
     *翻译：如果收到包含非法标头名称或值（例如标头名称不是令牌）的 HTTP 请求，是否应该拒绝该请求（使用 400 响应）还是应该忽略非法标头？
     *
     * setRejectIllegalHeader 是用来设置是否拒绝包含非法头部名称或值的HTTP请求的
     *
     * @param rejectIllegalHeader {@code true} to reject requests with illegal header names or values, {@code false} to
     *                                ignore the header
     *
     * @deprecated This will removed in Tomcat 11 onwards where {@code allowHostHeaderMismatch} will be hard-coded to
     *                 {@code true}.
     */
    @Deprecated
    public void setRejectIllegalHeader(boolean rejectIllegalHeader) {
        this.rejectIllegalHeader = rejectIllegalHeader;
    }




    // ------------------------------------------------ HTTP specific properties
    // ------------------------------------------ passed through to the EndPoint

    public boolean isSSLEnabled() {
        return getEndpoint().isSSLEnabled();
    }



    /**
     * The protocols that are available via internal Tomcat support for access via HTTP upgrade.
     */
    private final Map<String,UpgradeProtocol> httpUpgradeProtocols = new HashMap<>();
    /**
     * The protocols that are available via internal Tomcat support for access via ALPN negotiation.
     */
    private final Map<String,UpgradeProtocol> negotiatedProtocols = new HashMap<>();
    @Override
    public UpgradeProtocol getNegotiatedProtocol(String negotiatedName) {
        return negotiatedProtocols.get(negotiatedName);
    }

    @Override
    public UpgradeProtocol getUpgradeProtocol(String upgradedName) {
        return httpUpgradeProtocols.get(upgradedName);
    }


    // ------------------------------------------------------------- Common code

    @Override
    protected Processor createProcessor() {
//        Http11Processor processor = new Http11Processor(this, adapter);
//        return processor;
        throw new UnsupportedOperationException();
    }


    @Override
    protected Processor createUpgradeProcessor(SocketWrapperBase<?> socket, UpgradeToken upgradeToken) {
//        HttpUpgradeHandler httpUpgradeHandler = upgradeToken.getHttpUpgradeHandler();
//        if (httpUpgradeHandler instanceof InternalHttpUpgradeHandler) {
//            return new UpgradeProcessorInternal(socket, upgradeToken, getUpgradeGroupInfo(upgradeToken.getProtocol()));
//        } else {
//            return new UpgradeProcessorExternal(socket, upgradeToken, getUpgradeGroupInfo(upgradeToken.getProtocol()));
//        }
        throw new UnsupportedOperationException();
    }
}
