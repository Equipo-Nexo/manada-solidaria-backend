
package com.nexo.manada_solidaria_backend.auth.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.ObjectPostProcessor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.webauthn.authentication.WebAuthnAuthenticationFilter;
import org.springframework.security.web.webauthn.management.JdbcPublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.JdbcUserCredentialRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

import static com.nexo.manada_solidaria_backend.auth.utils.WhitelistUtils.ENDPOINTS_WHITELIST;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${security.cors.allowed-origins}")
            List<String> allowedOrigins,

            @Value("${security.cors.allowed-credentials}")
            boolean allowCredentials,

            @Value("${security.cors.allowed-origin-patterns:}")
            List<String> allowedOriginPatterns
    ) {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedOriginPatterns(allowedOriginPatterns);
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        );
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(allowCredentials);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider(
            UserDetailsService userDetailsService
    ) {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationProvider daoAuthenticationProvider,
            AuthenticationProvider bearerAuthenticationProvider
    ) {
        return new ProviderManager(
                List.of(
                        daoAuthenticationProvider,
                        bearerAuthenticationProvider
                )
        );
    }

    @Bean
    public JdbcPublicKeyCredentialUserEntityRepository publicKeyCredentialUserEntityRepository(JdbcOperations jdbc) {
        return new JdbcPublicKeyCredentialUserEntityRepository(jdbc);
    }

    @Bean
    public JdbcUserCredentialRepository userCredentialRepository(
            JdbcOperations jdbc
    ) {
        return new JdbcUserCredentialRepository(jdbc);
    }

    @Bean
    @Order(1)
    public SecurityFilterChain authenticationSecurityFilterChain(
            HttpSecurity http,
            OncePerRequestFilter bearerTokenFilter,
            AuthenticationEntryPoint customBasicAuthenticationEntryPoint,
            AuthenticationSuccessHandler webAuthnService,
            @Value("${security.webauthn.rp-id}") String rpId,
            @Value("${security.webauthn.allowed-origins}") String allowedOrigins
    ) throws Exception {

        return http
                .securityMatcher(
                        "/login",
                        "/auth/**",
                        "/webauthn/**",
                        "/login/webauthn"
                )
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ENDPOINTS_WHITELIST).permitAll()
                        .anyRequest().authenticated()
                )
                .webAuthn(webAuthn -> webAuthn
                        .rpName("Manada Solidaria")
                        .rpId(rpId)
                        .allowedOrigins(allowedOrigins)
                        .withObjectPostProcessor(
                                new ObjectPostProcessor<WebAuthnAuthenticationFilter>() {
                                    @Override
                                    public <O extends WebAuthnAuthenticationFilter>
                                    O postProcess(O filter) {

                                        filter.setAuthenticationSuccessHandler(
                                                webAuthnService
                                        );

                                        return filter;
                                    }
                                }
                        )
                )
                .addFilterBefore(
                        bearerTokenFilter,
                        BasicAuthenticationFilter.class
                )
                .httpBasic(basic -> basic
                        .authenticationEntryPoint(
                                customBasicAuthenticationEntryPoint
                        )
                )
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(
            HttpSecurity http,
            OncePerRequestFilter bearerTokenFilter
    ) throws Exception {

        return http
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ENDPOINTS_WHITELIST).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .addFilterBefore(
                        bearerTokenFilter,
                        BasicAuthenticationFilter.class
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }
}
