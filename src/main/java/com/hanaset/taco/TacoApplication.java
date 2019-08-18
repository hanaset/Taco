package com.hanaset.taco;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class TacoApplication {


    private final Environment environment;

    public TacoApplication(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(TacoApplication.class, args);
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadNamePrefix("tacoExecutor-");
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setQueueCapacity(50);
        taskExecutor.setMaxPoolSize(20);

        return taskExecutor;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void applicationReadyEvent() {
        log.info("applicationReady profiles:{}", Arrays.toString(environment.getActiveProfiles()));
    }

}
