package za.ac.cput.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.headers(h -> h.frameOptions(f -> f.disable()));
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.httpBasic(b -> {});            // ok during dev
        http.formLogin(f -> f.disable());
        http.authorizeHttpRequests(auth -> auth
                // matchers are WITHOUT /api/v1
                        .requestMatchers("/doctor/availability", "/doctor/timeoff", "/doctors/**", "/patients/**", "/appointments/**", "/auth/**", "/ping", "/h2-console/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()

                // <-- open everything to guarantee startup; tighten later
        );
        return http.build();
    }
}
