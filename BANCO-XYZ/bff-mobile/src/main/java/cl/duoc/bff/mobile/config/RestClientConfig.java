package cl.duoc.bff.mobile.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${banco-core.base-url}")
    private String coreUrl;

    @Bean
    public RestClient coreRestClient() {
        return RestClient.builder()
                .baseUrl(coreUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}