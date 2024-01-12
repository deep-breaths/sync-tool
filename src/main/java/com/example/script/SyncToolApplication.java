package com.example.script;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class SyncToolApplication {

    public static void main(String[] args) {


        SpringApplication.run(SyncToolApplication.class, args);

    }


}
