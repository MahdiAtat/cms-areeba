package com.areeba.cms.cmsmircoservice.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

import java.util.Collection;
import java.util.List;

@Configuration
public class FraudFeignConfig {

    @Bean
    RequestInterceptor oauth2FeignInterceptor(
            OAuth2AuthorizedClientManager manager) {
        return template -> {
            var attr = OAuth2AuthorizeRequest.withClientRegistrationId("fraud")
                    .principal(new Authentication() {
                        @Override public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(); }
                        @Override public Object getCredentials() { return ""; }
                        @Override public Object getDetails() { return null; }
                        @Override public Object getPrincipal() { return "cms-transactions"; }
                        @Override public boolean isAuthenticated() { return true; }
                        @Override public void setAuthenticated(boolean isAuthenticated) {}
                        @Override public String getName() { return "cms-transactions"; }
                    })
                    .build();
            var client = manager.authorize(attr);
            if (client == null) throw new IllegalStateException("Cannot obtain client credentials token for fraud");
            String token = client.getAccessToken().getTokenValue();
            template.header("Authorization", "Bearer " + token);
        };
    }

    @Bean
    OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository registrations,
            OAuth2AuthorizedClientRepository clients) {
        var provider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials().build();
        var mgr = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                registrations, new InMemoryOAuth2AuthorizedClientService(registrations));
        mgr.setAuthorizedClientProvider(provider);
        return mgr;
    }
}
