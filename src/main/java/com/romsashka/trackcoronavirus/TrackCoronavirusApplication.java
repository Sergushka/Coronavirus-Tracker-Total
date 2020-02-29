package com.romsashka.trackcoronavirus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrackCoronavirusApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrackCoronavirusApplication.class, args);
    }

}
