package com.designwright.research.microserviceplatform.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import java.net.MalformedURLException;
//import java.net.URL;

@SpringBootApplication
public class MicroserviceApplication {

	public static void main(String[] args) {//throws MalformedURLException {
//		URL test = new URL(null, "file:///home/atrum/dev/java/builds/rabbit-utils/rabbit-utils-0.0.1-SNAPSHOT.jar", new LoaderHandler(ClassLoader.getSystemClassLoader()));
//		Thread.currentThread().getClass().getClassLoader()

		SpringApplication.run(MicroserviceApplication.class, args);
	}
}
