package org.apache.tomcat.util.digester;

import org.xml.sax.Attributes;

/**
 * Concrete implementations of this class implement actions to be taken when
 * a corresponding nested pattern of XML elements has been matched.
 */
public abstract class Rule {


    /**
     * This method is called when the beginning of a matching XML element
     * is encountered. The default implementation is a NO-OP.
     *
     * @param namespace the namespace URI of the matching element, or an
     *                  empty string if the parser is not namespace aware or the
     *                  element has no namespace
     * @param name the local name if the parser is namespace aware, or just
     *             the element name otherwise
     * @param attributes The attribute list of this element
     *
     * @throws Exception if an error occurs while processing the event
     */
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        // NO-OP by default.
    }
}
