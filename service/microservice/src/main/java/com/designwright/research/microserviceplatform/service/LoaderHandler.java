//package com.designwright.research.service;
//
//import java.io.IOException;
//import java.net.URL;
//import java.net.URLConnection;
//import java.net.URLStreamHandler;
//
//public class LoaderHandler extends URLStreamHandler {
//
//    private final ClassLoader classLoader;
//
//    public LoaderHandler() {
//        this.classLoader = getClass().getClassLoader();
//    }
//
//    public LoaderHandler(ClassLoader classLoader) {
//        this.classLoader = classLoader;
//    }
//
//    @Override
//    protected URLConnection openConnection(URL u) throws IOException {
//        final URL resourceUrl = classLoader.getResource(u.getPath());
//        if( resourceUrl != null )
//            return resourceUrl.openConnection();
//        throw new NullPointerException("getResource() failed to return data for " + u.getPath());
//    }
//}
