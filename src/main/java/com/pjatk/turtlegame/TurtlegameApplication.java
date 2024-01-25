package com.pjatk.turtlegame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TurtlegameApplication {

    public static void main(String[] args) {
        SpringApplication.run(TurtlegameApplication.class, args);
    }


}
