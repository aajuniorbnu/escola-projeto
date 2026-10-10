package com.superdev.escolaprojeto.config;

import jakarta.servlet.http.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(
            new org.springframework.web.servlet.HandlerInterceptor() {
              @Override
              public boolean preHandle(
                  HttpServletRequest r, HttpServletResponse response, Object handler) {
                for (String name : java.util.List.of("page", "size"))
                  if (r.getParameter(name) != null) {
                    try {
                      int n = Integer.parseInt(r.getParameter(name));
                      if (n < (name.equals("page") ? 0 : 1) || (name.equals("size") && n > 100))
                        throw new NumberFormatException();
                    } catch (NumberFormatException e) {
                      throw new IllegalArgumentException(
                          "Paginação inválida: page >= 0; size entre 1 e 100.");
                    }
                  }
                return true;
              }
            })
        .addPathPatterns("/api/v1/**");
  }
}
