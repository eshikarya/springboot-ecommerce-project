package com.ecommerce.project.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme bearerSecurityScheme = new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT").description("JWT Bearer Token");

        SecurityRequirement bearerRequirement = new SecurityRequirement().addList("Bearer Authentication");

        return new OpenAPI()
                .info(
                        new Info().title("SpringBoot E-Commerce API")
                                .version("1.0")
                                .description("This is a sample springboot project for ecommerce")
                                .contact(new Contact().name("Jane Doe").email("jane.doe@gmail.com"))
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Project Documentation")
                        .url("https://github.com/eshikarya/springboot-ecommerce-project/tree/sb-ecom-2026"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", bearerSecurityScheme)).addSecurityItem(bearerRequirement);
    }
}
