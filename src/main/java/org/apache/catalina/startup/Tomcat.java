package org.apache.catalina.startup;

import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.core.StandardService;
import org.apache.tomcat.util.file.ConfigFileLoader;

import java.io.File;
import java.io.IOException;

public class Tomcat {
    protected String basedir;


    protected Server server;

    public void start() throws LifecycleException {
        getServer();
        server.start();
    }

    public Server getServer() {

        if (server != null) {
            return server;
        }

        System.setProperty("catalina.useNaming", "false");

        server = new StandardServer();

        initBaseDir();

        // Set configuration source
        ConfigFileLoader.setSource(new CatalinaBaseConfigurationSource(new File(basedir), null));

        server.setPort( -1 );

        Service service = new StandardService();
        service.setName("Tomcat");
        server.addService(service);
        return server;
    }

    protected void initBaseDir() {
//        String catalinaHome = System.getProperty(Globals.CATALINA_HOME_PROP);
//        if (basedir == null) {
//            basedir = System.getProperty(Globals.CATALINA_BASE_PROP);
//        }
//        if (basedir == null) {
//            basedir = catalinaHome;
//        }
//        if (basedir == null) {
//            // Create a temp dir.
//            basedir = System.getProperty("user.dir") + "/tomcat." + port;
//        }
//
//        File baseFile = new File(basedir);
//        if (baseFile.exists()) {
//            if (!baseFile.isDirectory()) {
//                throw new IllegalArgumentException(sm.getString("tomcat.baseDirNotDir", baseFile));
//            }
//        } else {
//            if (!baseFile.mkdirs()) {
//                // Failed to create base directory
//                throw new IllegalStateException(sm.getString("tomcat.baseDirMakeFail", baseFile));
//            }
//            /*
//             * If file permissions were going to be set on the newly created
//             * directory, this is the place to do it. However, even simple
//             * calls such as File.setReadable(boolean,boolean) behaves
//             * differently on different platforms. Therefore, setBaseDir
//             * documents that the user needs to do this.
//             */
//        }
//        try {
//            baseFile = baseFile.getCanonicalFile();
//        } catch (IOException e) {
//            baseFile = baseFile.getAbsoluteFile();
//        }
//        server.setCatalinaBase(baseFile);
//        System.setProperty(Globals.CATALINA_BASE_PROP, baseFile.getPath());
//        basedir = baseFile.getPath();
//
//        if (catalinaHome == null) {
//            server.setCatalinaHome(baseFile);
//        } else {
//            File homeFile = new File(catalinaHome);
//            if (!homeFile.isDirectory() && !homeFile.mkdirs()) {
//                // Failed to create home directory
//                throw new IllegalStateException(sm.getString("tomcat.homeDirMakeFail", homeFile));
//            }
//            try {
//                homeFile = homeFile.getCanonicalFile();
//            } catch (IOException e) {
//                homeFile = homeFile.getAbsoluteFile();
//            }
//            server.setCatalinaHome(homeFile);
//        }
//        System.setProperty(Globals.CATALINA_HOME_PROP,
//                server.getCatalinaHome().getPath());
    }
}
