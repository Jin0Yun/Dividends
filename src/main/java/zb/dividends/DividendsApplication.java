package zb.dividends;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DividendsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DividendsApplication.class, args);
    }
}
