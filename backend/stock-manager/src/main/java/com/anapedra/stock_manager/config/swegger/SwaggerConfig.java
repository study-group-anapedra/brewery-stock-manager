package com.anapedra.stock_manager.config.swegger;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI breweryInventoryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Brewery Inventory API")
                        .description("API for managing brewery stock — including beers, categories, orders, and restocking.")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Ana Santana")
                                .url("https://github.com/anapedra")
                                .email("ana.santana@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .externalDocs(new ExternalDocumentation()
                        .description("Full API documentation for the Brewery Inventory Management System")
                        .url("https://github.com/anapedra/brewery-inventory"));
    }
}
