package com.ninyo.common.rest.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class SwaggerUtils {

    @Value("${api.base.package:#{null}}")
    private String basePackage;

    @Value("${api.title:#{null}}")
    private String apiTitle;

    @Value("${api.description:#{null}}")
    private String apiDescription;

    @Value("${api.version:#{null}}")
    private String apiVersion;

    public Docket getApi() {
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
                .apiInfo(getApiInfo())
                .globalOperationParameters(getOperationParametersList());
    }

    public ApiInfo getApiInfo() {
        ApiInfoBuilder apiInfoBuilder = new ApiInfoBuilder()
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0");

        if (apiVersion != null) {
            apiInfoBuilder.version(apiVersion);
        }
        if (apiTitle != null) {
            apiInfoBuilder.title(apiTitle);
        }
        if (apiDescription != null) {
            apiInfoBuilder.description(apiDescription);
        }

        return apiInfoBuilder.build();
    }

    public List<Parameter> getOperationParametersList() {
        return newArrayList(new ParameterBuilder()
                        .name("X-Node-Id")
                        .description("Node Id")
                        .modelRef(new ModelRef("string"))
                        .parameterType("header")
                        .required(true)
                        .defaultValue("1")
                        .build(),
                new ParameterBuilder()
                        .name("X-Source-Id")
                        .description("Source Id")
                        .modelRef(new ModelRef("string"))
                        .parameterType("header")
                        .required(true)
                        .defaultValue("1")
                        .build(),
                new ParameterBuilder()
                        .name("X-Tenant-Id")
                        .description("Tenant Id")
                        .modelRef(new ModelRef("string"))
                        .parameterType("header")
                        .required(true)
                        .defaultValue("1")
                        .build(),
                new ParameterBuilder()
                        .name("X-Origin-Id")
                        .description("Origin Id")
                        .modelRef(new ModelRef("string"))
                        .parameterType("header")
                        .required(true)
                        .defaultValue("1")
                        .build());
    }

}
