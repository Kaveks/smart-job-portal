package com.kaveks.jobservice.config.clients;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class CompanyServiceClient {

    private final RestClient companyServiceRestClient;
    private final ObjectMapper objectMapper;

    public void verifyCompanyOwnership(Long companyId, String accessToken) {
        try {
            companyServiceRestClient.get()
                    .uri("/api/v1/companies/{id}/ownership", companyId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .toBodilessEntity();

        } catch (HttpStatusCodeException exception) {
            handleCompanyServiceException(exception);
        }
    }

    public void verifyCompanyEligibility(Long companyId,String accessToken) {
        try {
            companyServiceRestClient.get()
                    .uri("/api/v1/companies/{id}/eligibility", companyId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .toBodilessEntity();

        } catch (HttpStatusCodeException exception) {
            handleCompanyServiceException(exception);
        }
    }

    private void handleCompanyServiceException(HttpStatusCodeException exception) {
        String responseBody = exception.getResponseBodyAsString();
        String message = exception.getStatusText();

        try {
            JsonNode response = objectMapper.readTree(responseBody);

            if (response.hasNonNull("detail")) {
                message = response.get("detail").asString();
            } else if (response.hasNonNull("title")) {
                message = response.get("title").asString();
            }

        } catch (Exception ignored) {
            // Use the HTTP status text if the response isn't valid JSON.
        }

        throw new ResponseStatusException(
                exception.getStatusCode(),
                message,
                exception
        );
    }
}
