/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.mavenproject1;

import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

/**
 *
 * @author Ivan Pratama
 */
public class WebServer {
    private int port;
    private String webDirectory;
    private String logDirectory;
    private HttpServer server;


    public WebServer(int port, String webDirectory, String logDirectory) {
        this.port = port;
        this.webDirectory = webDirectory;
        this.logDirectory = logDirectory;
        
    }

    public void start() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/",new RequestHandler(webDirectory, logDirectory));
            server.setExecutor(Executors.newFixedThreadPool(10));
            server.start();
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void stop() {
        if (server != null) {
            server.stop(0); // Stop the server with a delay of 0 milliseconds
            System.out.println("Server stopped");
        }
    }
}
