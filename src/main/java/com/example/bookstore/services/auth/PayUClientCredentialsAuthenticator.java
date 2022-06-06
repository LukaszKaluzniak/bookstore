package com.example.bookstore.services.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PayUClientCredentialsAuthenticator {

    public static final String GRANT_TYPE = "client_credentials";

    private final RestTemplate restTemplate = new RestTemplate();

    @SneakyThrows
    public PayUAuthToken authenticate() {
        final String authRequest = new AuthTokenRequestBuilder()
                .authorizationUri("https://secure.payu.com/pl/standard/user/oauth/authorize")
                .clientId("145227")
                .clientSecret("12f071174cb7eb79d4aac5bc2f07563f")
                .grantType(GRANT_TYPE)
                .build();

        final ResponseEntity<String> jsonResponse = restTemplate.postForEntity(authRequest, null, String.class);
        return new ObjectMapper().readValue(jsonResponse.getBody(), PayUAuthToken.class);
    }

    public static class AuthTokenRequestBuilder {

        private String authorizationUri;
        private String clientId;
        private String clientSecret;
        private String grantType;

        public AuthTokenRequestBuilder authorizationUri(final String authorizationUri) {
            this.authorizationUri = authorizationUri;
            return this;
        }

        public AuthTokenRequestBuilder clientId(final String clientId) {
            this.clientId = clientId;
            return this;
        }

        public AuthTokenRequestBuilder clientSecret(final String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public AuthTokenRequestBuilder grantType(final String grantType) {
            this.grantType = grantType;
            return this;
        }

        public String build() {
            return authorizationUri +
                    "?grant_type=" + grantType +
                    "&client_id=" + clientId +
                    "&client_secret=" + clientSecret;
        }
    }
}
