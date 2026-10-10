package com.superdev.escolaprojeto;

import java.time.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EscolaApplication {
  public static void main(String[] args) {
    SpringApplication.run(EscolaApplication.class, args);
  }

  @Bean
  Clock clock(@Value("${app.zone}") String zone) {
    return Clock.system(ZoneId.of(zone));
  }
}
