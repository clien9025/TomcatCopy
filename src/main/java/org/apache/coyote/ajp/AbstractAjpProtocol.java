package org.apache.coyote.ajp;

import org.apache.coyote.AbstractProtocol;

/**
 * The is the base implementation for the AJP protocol handlers. Implementations typically extend this base class rather
 * than implement {@link org.apache.coyote.ProtocolHandler}. All of the implementations that ship with Tomcat are
 * implemented this way.
 *
 * @param <S> The type of socket used by the implementation
 */
public abstract class AbstractAjpProtocol<S> extends AbstractProtocol<S> {
}
