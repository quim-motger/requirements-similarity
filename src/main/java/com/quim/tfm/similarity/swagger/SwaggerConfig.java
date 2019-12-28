package com.quim.tfm.similarity.swagger;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(regex("/.*"))
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                "Requirements Similarity detection tool",
                "\n" +
                        "The Requirements Similarity system is a Java-based web service deployed as a REST API that " +
                        "allows software engineers to test and evaluate similarity algorithms between requirement " +
                        "items in a software engineering (SE) project.",
                "1.0.0",
                null,
                "jmotger@uoc.edu",
                "APACHE LICENSE, VERSION 2.0",
                "https://www.apache.org/licenses/LICENSE-2.0.txt"
        );
        return apiInfo;
    }

}
