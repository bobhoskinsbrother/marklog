package uk.co.itstherules.resourceserver;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

import java.io.File;
import java.net.URI;

public final class ResourceServer {

    private final Server server;

    public static void main(String[] args) {
        ResourceServer resourceServer = new ResourceServer(new File("/home/ben/Dropbox/Code/java/marklog/demo_marklog_blog"));
        System.out.println(resourceServer.start());
        resourceServer.join();
    }

    public ResourceServer(File baseFile) {
        try {
            server = new Server(0);
            ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.setDirectoriesListed(true);
            resourceHandler.setWelcomeFiles(new String[]{"index.html"});
            resourceHandler.setResourceBase(baseFile.getCanonicalPath());
            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[]{resourceHandler, new DefaultHandler()});
            server.setHandler(handlers);
            server.setStopAtShutdown(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public URI start() {
        try {
            server.start();
            return uri();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public URI uri() {
        return server.getURI();
    }

    public void join() {
        try {
            server.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}