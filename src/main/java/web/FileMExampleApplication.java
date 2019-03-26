package web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author jianglinzou
 * @date 2019/3/22 下午10:47
 */

@EnableZuulProxy
@SpringBootApplication
public class FileMExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileMExampleApplication.class, args);
    }

}
