package barcute.rest;
import barcute.persistence.JdbcUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan("barcute")
public class StartServices {
    public static void main(String[] args) {
        JdbcUtils.initialize();
        SpringApplication.run(StartServices.class, args);
    }
}