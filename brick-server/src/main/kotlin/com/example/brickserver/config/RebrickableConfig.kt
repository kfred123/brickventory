package com.example.brickserver.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class RebrickableConfig(
    @Value("\${rebrickable.api.key:}") val apiKey: String,
    @Value("\${rebrickable.api.url:https://rebrickable.com/api/v3/}") val baseUrl: String
) {

    @Bean
    fun rebrickableRestClient(builder: RestClient.Builder): RestClient {
        return builder
            .baseUrl(baseUrl)
            .defaultHeader("Authorization", "key $apiKey")
            .build()
    }
}
