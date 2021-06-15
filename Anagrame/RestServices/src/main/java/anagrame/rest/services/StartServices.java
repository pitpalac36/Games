package anagrame.rest.services;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan("anagrame")
public class StartServices {

    public static void main(String[] args) {

        //SpringApplication.run(StartServices.class, args);
    }
}