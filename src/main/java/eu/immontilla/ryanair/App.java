package eu.immontilla.ryanair;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableCaching
@EnableSwagger2
public class App {
    @Value("${swagger2.title}")
    String swaggerTitle;
    @Value("${swagger2.description}")
    String swaggerDescription;
    @Value("${swagger2.author.name}")
    String swaggerAuthorName;
    @Value("${swagger2.author.web}")
    String swaggerAuthorWeb;
    @Value("${swagger2.author.email}")
    String swaggerAuthorEmail;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    
    @Bean
    public Docket interconnectionsAPI() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("eu.immontilla.ryanair.controller"))
                .paths(PathSelectors.regex("/api/interconnections.*")).build().apiInfo(metaData());
    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder().title(swaggerTitle).description(swaggerDescription)
                .contact(new Contact(swaggerAuthorName, swaggerAuthorWeb, swaggerAuthorEmail)).build();
    }
}
