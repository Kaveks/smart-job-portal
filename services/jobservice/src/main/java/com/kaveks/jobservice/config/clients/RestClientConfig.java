
package com.kaveks.jobservice.config.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient companyServiceRestClient(
            @Value("${app.services.company.base-url}") String companyServiceUrl
    ) {
        return RestClient.builder()
                .baseUrl(companyServiceUrl)
                .build();
    }
}
