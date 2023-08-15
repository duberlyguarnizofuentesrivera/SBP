package com.duberlyguarnizo.sbp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableWebSecurity
@Slf4j
public class SbpApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SbpApplication.class);
        application.addListeners(new MyFailedEvent());
        application.run(args);
    }

    @Bean
    CommandLineRunner runner(MyMessage messager) {

        return args -> {
            log.warn("getting message from CLR:");
            log.warn(messager.message);};
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req.anyRequest().permitAll())
                .httpBasic(httpSecurityHttpBasicConfigurer -> {
                })
                .build();
    }

    @EventListener(ApplicationStartedEvent.class)
    private void preparedEventListener(ApplicationStartedEvent event) {
        log.warn("this was sent when prepared!");
    }

    @EventListener(ApplicationFailedEvent.class)
    private void failureEventListener(ApplicationFailedEvent event) {
        String cause = event.getException().getMessage();
        log.warn("the application had a failure!!!, the cause was: {}", cause);
    }

}

@Slf4j
class MyFailedEvent implements ApplicationListener<ApplicationFailedEvent> {
    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        String cause = event.getException().getMessage();
        log.warn("the application had a failure (this message comes from a custom listener)!!!, the cause was: {}", cause);
    }
}


@Component
@PropertySource("classpath:error_messages.properties")
class MyMessage {
    @Value("${error.message}")
    String message;
}
