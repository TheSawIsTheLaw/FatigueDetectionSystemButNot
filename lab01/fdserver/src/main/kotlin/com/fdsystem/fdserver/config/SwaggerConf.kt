package com.fdsystem.fdserver.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


// http://localhost:8080/swagger-ui/

@Configuration
class OpenApiConfig
{
    @Bean
    fun customOpenAPI(): OpenAPI?
    {
        return OpenAPI()
            .components(Components())
            .info(
                Info().title("FDSystem (in russian СРУ, it's funny) API").description(
                    "All necessary operations for work in FDSystem. Welcome, subject ;-;"
                )
            )
    }
}