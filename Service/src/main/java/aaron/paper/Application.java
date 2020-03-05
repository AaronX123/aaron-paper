package aaron.paper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-03
 */

@EnableFeignClients
@EnableEurekaClient
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
@SpringBootApplication()
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
