package For.the.light.config;

import For.the.light.security.HttpCookieOAuth2AuthorizationRequestRepository;
import For.the.light.security.JwtAuthenticationFilter;
import For.the.light.security.OAuth2AuthenticationSuccessHandler;
import For.the.light.security.OAuth2AuthenticationFailureHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenValidator;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.web.SecurityFilterChain;
import java.time.Duration;
import java.util.List;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

        private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
        private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

        @Value("${app.custom.redirect-uri}")
        private String redirectUri;

        @Value("${app.custom.frontend-uri}")
        private String frontEndUri;

        @Value("${app.oauth2.id-token-clock-skew-seconds:180}")
        private long idTokenClockSkewSeconds;

        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless JWT API
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/public/**", "/oauth2/**", "/login**",
                                                                "/actuator/**", "/csrf")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/iot/webhook/**").permitAll()
                                                .requestMatchers("/api/iot/**").hasAuthority("SUPER_ADMIN")
                                                .requestMatchers(HttpMethod.GET, "/incident/available").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/incident/*").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/program/available").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/program/*").permitAll()
                                                .anyRequest().authenticated())
                                .oauth2Login(oauth -> oauth
                                                .authorizationEndpoint(authorization -> authorization
                                                                .baseUri("/oauth2/authorize")
                                                                .authorizationRequestRepository(
                                                                                httpCookieOAuth2AuthorizationRequestRepository))
                                                .redirectionEndpoint(redirection -> redirection
                                                                .baseUri("/login/oauth2/code/*"))
                                                .successHandler(oAuth2AuthenticationSuccessHandler)
                                                .failureHandler(oAuth2AuthenticationFailureHandler))
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        JwtDecoderFactory<ClientRegistration> idTokenDecoderFactory() {
                var jwtDecoderFactory = new OidcIdTokenDecoderFactory();

                jwtDecoderFactory.setJwtValidatorFactory(clientRegistration -> {
                        Duration clockSkew = Duration.ofSeconds(idTokenClockSkewSeconds);
                        OAuth2TokenValidator<Jwt> timestampValidator = new JwtTimestampValidator(clockSkew);
                        OidcIdTokenValidator oidcValidator = new OidcIdTokenValidator(clientRegistration);
                        oidcValidator.setClockSkew(clockSkew);
                        return new DelegatingOAuth2TokenValidator<>(timestampValidator, oidcValidator);
                });

                return jwtDecoderFactory;
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
                var config = new CorsConfiguration();
                config.setAllowedOrigins(List.of(frontEndUri));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);
                var source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return source;
        }
}
