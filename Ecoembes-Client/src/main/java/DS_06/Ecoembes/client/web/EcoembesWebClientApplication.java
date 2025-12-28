package DS_06.Ecoembes.client.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "DS_06.Ecoembes.client")
public class EcoembesWebClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcoembesWebClientApplication.class, args);
    }
}
