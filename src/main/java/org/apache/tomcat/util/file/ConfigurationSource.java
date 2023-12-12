package org.apache.tomcat.util.file;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public interface ConfigurationSource {

    ConfigurationSource DEFAULT = new ConfigurationSource() {
//        protected final File userDir = new File(System.getProperty("user.dir"));
//        protected final URI userDirUri = userDir.toURI();
//        @Override
//        public Resource getResource(String name) throws IOException {
//            if (!UriUtil.isAbsoluteURI(name)) {
//                File f = new File(name);
//                if (!f.isAbsolute()) {
//                    f = new File(userDir, name);
//                }
//                if (f.isFile()) {
//                    FileInputStream fis = new FileInputStream(f);
//                    return new Resource(fis, f.toURI());
//                }
//            }
//            URI uri = null;
//            try {
//                uri = userDirUri.resolve(name);
//            } catch (IllegalArgumentException e) {
//                throw new FileNotFoundException(name);
//            }
//            try {
//                URL url = uri.toURL();
//                return new Resource(url.openConnection().getInputStream(), uri);
//            } catch (MalformedURLException e) {
//                throw new FileNotFoundException(name);
//            }
//        }
//        @Override
//        public URI getURI(String name) {
//            if (!UriUtil.isAbsoluteURI(name)) {
//                File f = new File(name);
//                if (!f.isAbsolute()) {
//                    f = new File(userDir, name);
//                }
//                if (f.isFile()) {
//                    return f.toURI();
//                }
//            }
//            return userDirUri.resolve(name);
//        }
    };
    class Resource implements AutoCloseable {
        private final InputStream inputStream;
        private final URI uri;
        public Resource(InputStream inputStream, URI uri) {
            this.inputStream = inputStream;
            this.uri = uri;
        }
        public InputStream getInputStream() {
            return inputStream;
        }
        public URI getURI() {
            return uri;
        }
        public long getLastModified()
                throws MalformedURLException, IOException {
//            URLConnection connection = null;
//            try {
//                connection = uri.toURL().openConnection();
//                return connection.getLastModified();
//            } finally {
//                if (connection != null) {
//                    connection.getInputStream().close();
//                }
//            }
            throw new UnsupportedOperationException();
        }
        @Override
        public void close() throws IOException {
//            if (inputStream != null) {
//                inputStream.close();
//            }
            throw new UnsupportedOperationException();
        }
    }


}
