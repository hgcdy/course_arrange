package cn.netinnet.coursearrange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CourseArrangeApplication {

    private static Logger LOGGER = LoggerFactory.getLogger(CourseArrangeApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CourseArrangeApplication.class, args);
        LOGGER.info("-----自动排课系统启动完成-----");
    }

}
