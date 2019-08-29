package net.ameizi;

import net.ameizi.ratelimit.RateLimit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableAsync
@RestController
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * 1秒钟最多访问2次
     *
     * @return
     */
    @RateLimit(count = 2, time = 1)
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
