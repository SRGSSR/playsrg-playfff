package ch.srgssr.playfff.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

  private final ApplicationContext applicationContext;

  @Autowired
  public WebMvcConfig(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public void addViewControllers(final ViewControllerRegistry registry) {
    super.addViewControllers(registry);
    registry.addViewController("/updates").setViewName("forward:/");
    registry.addViewController("/add_update").setViewName("forward:/");
    registry.addViewController("/deeplink").setViewName("forward:/");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**")
        .addResourceLocations("/portal-app/dist/admin-app")
        .setCachePeriod(3600)
        .resourceChain(true)
        .addResolver(new PathResourceResolver() {
          @Override
          protected Resource getResource(String resourcePath, Resource location) throws IOException {
            Resource requestedResource = location.createRelative(resourcePath);
            return requestedResource.exists() && requestedResource.isReadable() ? requestedResource
                : applicationContext.getResource("/portal-app/dist/admin-app/index.html");
          }
        });
  }
}
