package com.ninyo.mongodb.service.config;

import com.ninyo.common.rest.utils.SwaggerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${api.base.package:#{null}}")
    private String basePackage;

    @Autowired
    private SwaggerUtils swaggerUtils;

    @Bean
    public Docket api() {
        ApiSelectorBuilder apiSelectorBuilder = new Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(PathSelectors.any());

        if (basePackage != null) {
            apiSelectorBuilder.apis(RequestHandlerSelectors.basePackage(basePackage));
        } else {
            apiSelectorBuilder.apis(RequestHandlerSelectors.any());
        }

        return apiSelectorBuilder
                .build()
                .apiInfo(swaggerUtils.getApiInfo())
                .globalOperationParameters(swaggerUtils.getOperationParametersList());
    }

}