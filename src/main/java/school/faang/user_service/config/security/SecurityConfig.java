package school.faang.user_service.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import school.faang.user_service.service.user.UserService;

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder encoder, UserService userService) {
        DaoAuthenticationProvider authProvide = new DaoAuthenticationProvider();
        authProvide.setUserDetailsService(userService);
        authProvide.setPasswordEncoder(encoder);
        return authProvide;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            SecurityFilterChainFilter jwtFilter,
            AuthenticationProvider authenticationProvider
    ) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests(
                        authorizeHttp -> {
                            authorizeHttp.requestMatchers("/").permitAll();
                            authorizeHttp.requestMatchers("/favicon.svg").permitAll();
                            authorizeHttp.requestMatchers("/css/*").permitAll();
                            authorizeHttp.requestMatchers(
                                    "/api/auth/**"
                            ).permitAll();
                            authorizeHttp.requestMatchers(
                                    "/v3/api-docs/**",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/v3/api-docs/swagger-config"
                            ).permitAll();
                            authorizeHttp.anyRequest().permitAll();
                        }
                )
                .sessionManagement(httpSecuritySessionManagementConfigurer -> {
                    httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}