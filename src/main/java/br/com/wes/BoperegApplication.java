package br.com.wes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BoperegApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoperegApplication.class, args);
    }

}
