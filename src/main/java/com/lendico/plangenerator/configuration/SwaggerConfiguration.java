package com.lendico.plangenerator.configuration;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.builders.PathSelectors.regex;

import com.google.common.base.Predicates;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.AuthorizationScopeBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/** Swagger integration to support Open API specification 2. */
@EnableSwagger2
@Configuration
public class SwaggerConfiguration {

  @Bean
  public Docket planApi(@Autowired BuildConfiguration buildConfiguration) {
    AuthorizationScope[] authScopes = new AuthorizationScope[1];
    authScopes[0] =
        new AuthorizationScopeBuilder().scope("read").description("read access").build();
    SecurityReference securityReference =
        SecurityReference.builder().reference("test").scopes(authScopes).build();

    ArrayList<SecurityContext> securityContexts =
        newArrayList(
            SecurityContext.builder().securityReferences(newArrayList(securityReference)).build());
    return new Docket(DocumentationType.SWAGGER_2)
        .securitySchemes(newArrayList(new BasicAuth("test")))
        .securityContexts(securityContexts)
        .apiInfo(apiInfo(buildConfiguration))
        .select()
        .paths(Predicates.or(regex("/plans.*")))
        .build();
  }

  private ApiInfo apiInfo(BuildConfiguration buildConfiguration) {
    return new ApiInfoBuilder()
        .title(buildConfiguration.getTitle())
        .description(buildConfiguration.getDescription())
        .version(buildConfiguration.getVersion())
        .build();
  }
}
