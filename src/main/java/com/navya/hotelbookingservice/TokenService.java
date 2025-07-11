package com.navya.hotelbookingservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TokenService
{
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);


    @Autowired
    @Qualifier("authValidateWebClient")
    WebClient authValidateWebClient;

    @Autowired
    WebClient authGetRoleFromTokenWebClient;

    public String validateToken(String token) throws WebClientResponseException
    {
        logger.info("TokenService.validateToken() called with token: " + token);
        return authValidateWebClient.get()
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(String.class)
                .block(); // Assuming the token is valid for demonstration purposes
    }

    public String getRoleFromToken(String token) throws WebClientResponseException
    {
        logger.info("TokenService.getRoleFromToken() called with token: " + token);
        return authGetRoleFromTokenWebClient.get()
                .uri(uriBuilder -> uriBuilder
                .path("/{token}").build(token))
                .retrieve()
                .bodyToMono(String.class)
                .block(); // Assuming the token is valid for demonstration purposes
    }


}
