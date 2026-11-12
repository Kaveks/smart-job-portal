
package com.kaveks.commonweb.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaveks.commonweb.security.annotation.AllowedRoles;
import com.kaveks.commonweb.security.authorization.RoleAuthorizationManager;
import com.kaveks.commonweb.security.exception.JwtAccessDeniedHandler;
import com.kaveks.commonweb.security.exception.JwtAuthenticationEntryPoint;

import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

@AutoConfiguration
@ConditionalOnClass(SecurityFilterChain.class)
@EnableConfigurationProperties(CommonSecurityProperties.class)
@EnableMethodSecurity
public class CommonSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AuthenticationEntryPoint.class)
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint(
            ObjectMapper objectMapper
    ) {
        return new JwtAuthenticationEntryPoint(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(AccessDeniedHandler.class)
    public JwtAccessDeniedHandler jwtAccessDeniedHandler(
            ObjectMapper objectMapper
    ) {
        return new JwtAccessDeniedHandler(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationConverter.class)
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter authenticationConverter =
                new JwtAuthenticationConverter();

        authenticationConverter.setJwtGrantedAuthoritiesConverter(
                authoritiesConverter
        );

        return authenticationConverter;
    }

    @Bean
    @ConditionalOnMissingBean(RoleAuthorizationManager.class)
    public RoleAuthorizationManager roleAuthorizationManager() {
        return new RoleAuthorizationManager();
    }

    @Bean
    @ConditionalOnMissingBean(
            name = "requireRolesAuthorizationInterceptor"
    )
    public AuthorizationManagerBeforeMethodInterceptor
    requireRolesAuthorizationInterceptor(
            RoleAuthorizationManager authorizationManager
    ) {

        ComposablePointcut pointcut = new ComposablePointcut(
                AnnotationMatchingPointcut.forClassAnnotation(
                        AllowedRoles.class
                )
        );

        pointcut.union(
                AnnotationMatchingPointcut.forMethodAnnotation(
                        AllowedRoles.class
                )
        );

        return new AuthorizationManagerBeforeMethodInterceptor(
                pointcut,
                authorizationManager
        );
    }

    @Bean
    @ConditionalOnBean(JwtDecoder.class)
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain commonSecurityFilterChain(
            HttpSecurity http,
            CommonSecurityProperties properties,
            JwtAuthenticationEntryPoint authenticationEntryPoint,
            JwtAccessDeniedHandler accessDeniedHandler,
            JwtAuthenticationConverter jwtAuthenticationConverter
    ) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> {
                    properties.getPublicEndpoints().forEach(endpoint ->
                            auth.requestMatchers(endpoint).permitAll()
                    );

                    auth.anyRequest().authenticated();
                })
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(
                                        jwtAuthenticationConverter
                                )
                        )
                );

        return http.build();
    }
}
