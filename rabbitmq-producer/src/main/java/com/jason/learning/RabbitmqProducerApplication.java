package com.jason.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// exclude = {DataSourceAutoConfiguration.class} 排除掉默认数据源
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RabbitmqProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqProducerApplication.class, args);
    }

}
