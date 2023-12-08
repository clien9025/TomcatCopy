package com.example.demowebserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class DemoWebserverApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(DemoWebserverApplication.class, args);

        System.out.println("started");
    }

}
