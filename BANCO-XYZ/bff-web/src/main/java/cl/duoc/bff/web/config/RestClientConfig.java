package cl.duoc.bff.web.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    @LoadBalanced // Permite que RestClient resuelva nombres de Eureka (ej: ms-gestion-clientes)
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder()
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json");
    }
}