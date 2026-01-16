package For.the.light.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.*;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.function.Supplier;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/", "/actuator", "/live").permitAll() // Home page is public
//                        .anyRequest().authenticated()     // All other endpoints require login
//                )
//                .oauth2Login(withDefaults());         // Enable OAuth2 login
//
//        return http.build();
//    }

        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                    .csrf(csrf -> csrf
                            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                    )
                    .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/api/public/**", "/oauth2/**", "/login**", "/actuator", "/csrf").permitAll()
                            .requestMatchers(HttpMethod.GET, "/user/details").authenticated()
                            .anyRequest().authenticated()
                    )
                    .oauth2Login(oauth -> oauth.defaultSuccessUrl("http://localhost:5173/dashboard", true))
                    .logout(logout -> logout
                            .logoutUrl("/logout")
                            .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                            .logoutSuccessHandler((req, res, auth) -> {
                                res.setStatus(HttpServletResponse.SC_OK);
                                res.setContentType("application/json");
                                res.getWriter().write("{\"message\":\"Logged out successfully\"}");
                            })
                    );
            return http.build();
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
            var config = new CorsConfiguration();
            config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(List.of("*")); // Allow all headers
            config.setExposedHeaders(List.of("X-XSRF-TOKEN")); // Expose CSRF token header
            config.setAllowCredentials(true);
            var source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", config);
            return source;
        }
    }

    // Add this handler class for SPA CSRF token handling
    final class SpaCsrfTokenRequestHandler extends CsrfTokenRequestAttributeHandler {
        private final CsrfTokenRequestHandler delegate = new XorCsrfTokenRequestAttributeHandler();

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response,
                           Supplier<CsrfToken> csrfToken) {
            this.delegate.handle(request, response, csrfToken);
        }

        @Override
        public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
            if (StringUtils.hasText(request.getHeader(csrfToken.getHeaderName()))) {
                return super.resolveCsrfTokenValue(request, csrfToken);
            }
            return this.delegate.resolveCsrfTokenValue(request, csrfToken);
        }
    }

//    @Bean
//    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
////                        .csrfTokenRequestHandler(new ServerCsrfTokenRequestAttributeHandler()::handle)
//                )
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/public/**", "/oauth2/**", "/login**", "/actuator").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/user/details").authenticated()
//                        .anyRequest().authenticated()
//                )
//                .oauth2Login(oauth -> oauth.defaultSuccessUrl("http://localhost:5173/dashboard", true))
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .deleteCookies("JSESSIONID")
//                        .logoutSuccessHandler(
//                                (
//                                        req,
//                                        res, auth
//                                ) -> res.setStatus(HttpServletResponse.SC_OK)
//                        )
//                );
//        return http.build();
//    }
//
//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        var config = new CorsConfiguration();
//        // Allow the Vite dev origin (add any other dev origins you use)
//        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        // Include the X-XSRF-TOKEN header that Spring expects when using CookieCsrfTokenRepository
//        config.setAllowedHeaders(List.of("Content-Type", "Authorization", "X-CSRF-TOKEN", "X-XSRF-TOKEN", "XSRF-TOKEN"));
//        config.setAllowCredentials(true);
//        var source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }

//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        var config = new CorsConfiguration();
//        config.setAllowedOrigins(List.of("http://localhost:5173"));
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        config.setAllowedHeaders(List.of("Content-Type", "Authorization", "X-CSRF-TOKEN"));
//        config.setAllowCredentials(true);
//        var source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }
//}
