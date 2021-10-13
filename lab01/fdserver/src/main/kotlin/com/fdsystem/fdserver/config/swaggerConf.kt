package com.fdsystem.fdserver.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


// http://localhost:8080/swagger-ui/

@Configuration
@EnableSwagger2
class ApplicationConfig: WebMvcConfigurationSupport()
{
    override fun addResourceHandlers(registry: ResourceHandlerRegistry)
    {
        registry.addResourceHandler("/swagger-ui/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
            .resourceChain(false)
    }

    override fun addViewControllers(registry: ViewControllerRegistry)
    {
        registry.addViewController("/swagger-ui/")
            .setViewName("forward:/swagger-ui/index.html")
    }

    @Bean
    fun api(): Docket
    {
        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .paths(PathSelectors.any())
            .apis(RequestHandlerSelectors.basePackage("com.fdsystem.fdserver.controller"))
            .build()
            .apiInfo(getApiInfo())
    }

    private fun getApiInfo(): ApiInfo
    {
        val contact = Contact("Yakuba Dmitry", "https://t.me/xGULZAx", "dimakrok@bk.ru")
        return ApiInfoBuilder()
            .title("FDSystem API Docs")
            .description("Esli bi vi znali kak dolgo ya muchalsya s etimi zavisimostyami - vi bi rasplakalis' :)")
            .version("1.0.0")
            .license("Apache 2.0")
            .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
            .contact(contact)
            .build()
    }
}