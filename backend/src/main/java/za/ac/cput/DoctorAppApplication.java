package za.ac.cput;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "za.ac.cput")
@EnableJpaRepositories(basePackages = "za.ac.cput")
@EntityScan(basePackages = "za.ac.cput")
public class DoctorAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(DoctorAppApplication.class, args);
    }
}
