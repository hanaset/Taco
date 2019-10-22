package com.hanaset.tacogenji.config;

import com.hanaset.TacoGenjiApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.Errors;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@ComponentScan(basePackageClasses = TacoGenjiApplication.class)
public class SwaggerConfig {

    @Bean
    public Docket docket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(Pageable.class, Errors.class)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.hanaset.tacogenji.web.rest"))
                .paths(PathSelectors.regex("/.*"))
                .build()
                .pathMapping("/")
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("GENJI Application")
                .description("API Documentation")
                .version("1.0")
                .build();
    }
}
