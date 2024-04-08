package org.adzironman.aidevs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
//@ImportAutoConfiguration({FeignAutoConfiguration.class, FeignClientConfiguration.class})
public class AidevApplication {

    public static void main(String[] args) {
        SpringApplication.run(AidevApplication.class, args);
    }

}
