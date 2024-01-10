package org.apache.tomcat.util.scan;

import org.apache.tomcat.JarScanner;

/**
 * The default {@link JarScanner} implementation scans the WEB-INF/lib directory
 * followed by the provided classloader and then works up the classloader
 * hierarchy. This implementation is sufficient to meet the requirements of the
 * Servlet 3.0 specification as well as to provide a number of Tomcat specific
 * extensions. The extensions are:
 * <ul>
 *   <li>Scanning the classloader hierarchy (enabled by default)</li>
 *   <li>Testing all files to see if they are JARs (disabled by default)</li>
 *   <li>Testing all directories to see if they are exploded JARs
 *       (disabled by default)</li>
 * </ul>
 * All of the extensions may be controlled via configuration.
 */
public class StandardJarScanner implements JarScanner {

}
