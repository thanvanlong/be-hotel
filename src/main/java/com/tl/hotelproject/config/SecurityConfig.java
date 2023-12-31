package com.tl.hotelproject.config;

import com.tl.hotelproject.entity.user.Role;
import com.tl.hotelproject.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder bCryptPasswordEncoder;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService);
        authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return authenticationProvider;
    }

    @Bean
    public ProviderManager authManagerBean(AuthenticationProvider provider) {
        return new ProviderManager(provider);
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return new ProviderManager(authProvider);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customeAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager());
        customeAuthenticationFilter.setFilterProcessesUrl("/api/v1/users/login");
        http.csrf().disable();
        http.cors();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
        http.authorizeRequests().requestMatchers(HttpMethod.GET, "/api/v1/users/**").permitAll();
        http.authorizeRequests().requestMatchers(HttpMethod.GET, "/api/v1/booking/**").permitAll();
        http.authorizeRequests().requestMatchers(HttpMethod.GET, "/api/v1/room/**").permitAll();
        http.authorizeRequests().requestMatchers(HttpMethod.GET, "/api/v1/services/**").permitAll();
        http.authorizeRequests().requestMatchers(HttpMethod.GET, "/api/v1/promotion/**").permitAll();
        http.authorizeRequests().requestMatchers(HttpMethod.GET, "/api/v1/client/**").permitAll();
        http.authorizeRequests().requestMatchers(HttpMethod.GET, "/api/v1/stats/**").hasAuthority("ROLE_ADMIN");
        http.authorizeRequests().requestMatchers(HttpMethod.GET, "/api/v1/stats/**").authenticated();

        http.authorizeRequests().requestMatchers(HttpMethod.POST, "/api/v1/users/**").permitAll();
        http.authorizeRequests().requestMatchers(HttpMethod.POST, "/api/v1/booking/client-booking").permitAll();
        http.authorizeRequests().requestMatchers(HttpMethod.POST, "/api/v1/booking/**").authenticated();
        http.authorizeRequests().requestMatchers(HttpMethod.POST, "/api/v1/room/**").hasAnyAuthority(Role.ROLE_ADMIN.name(), Role.ROLE_USER.name());
        http.authorizeRequests().requestMatchers(HttpMethod.POST, "/api/v1/room/**").authenticated();
        http.authorizeRequests().requestMatchers(HttpMethod.POST, "/api/v1/services/**").hasAnyAuthority(Role.ROLE_ADMIN.name(), Role.ROLE_USER.name());
        http.authorizeRequests().requestMatchers(HttpMethod.POST, "/api/v1/services/**").authenticated();
        http.authorizeRequests().requestMatchers(HttpMethod.POST, "/api/v1/promotion/**").authenticated();
        http.authorizeRequests().requestMatchers(HttpMethod.POST, "/api/v1/client/**").authenticated();

        http.authorizeRequests().requestMatchers(HttpMethod.PUT, "/api/v1/users/**").permitAll();
        http.authorizeRequests().requestMatchers(HttpMethod.PUT, "/api/v1/booking/**").authenticated();
//        http.authorizeRequests().requestMatchers(HttpMethod.PUT, "/api/v1/room/**").hasAnyAuthority(Role.ROLE_ADMIN.name(), Role.ROLE_USER.name());
        http.authorizeRequests().requestMatchers(HttpMethod.PUT, "/api/v1/room/**").authenticated();
//        http.authorizeRequests().requestMatchers(HttpMethod.PUT, "/api/v1/services/**").hasAnyAuthority(Role.ROLE_ADMIN.name(), Role.ROLE_USER.name());
        http.authorizeRequests().requestMatchers(HttpMethod.PUT, "/api/v1/services/**").authenticated();
        http.authorizeRequests().requestMatchers(HttpMethod.PUT, "/api/v1/promotion/**").authenticated();
        http.authorizeRequests().requestMatchers(HttpMethod.PUT, "/api/v1/client/**").authenticated();

        http.authorizeRequests().requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").permitAll();
        http.authorizeRequests().requestMatchers(HttpMethod.DELETE, "/api/v1/booking/**").authenticated();
//        http.authorizeRequests().requestMatchers(HttpMethod.DELETE, "/api/v1/room/**").hasAnyAuthority(Role.ROLE_ADMIN.name(), Role.ROLE_USER.name());
        http.authorizeRequests().requestMatchers(HttpMethod.DELETE, "/api/v1/room/**").authenticated();
//        http.authorizeRequests().requestMatchers(HttpMethod.DELETE, "/api/v1/services/**").hasAnyAuthority(Role.ROLE_ADMIN.name(), Role.ROLE_USER.name());
        http.authorizeRequests().requestMatchers(HttpMethod.DELETE, "/api/v1/services/**").authenticated();
        http.authorizeRequests().requestMatchers(HttpMethod.DELETE, "/api/v1/promotion/**").authenticated();
        http.authorizeRequests().requestMatchers(HttpMethod.DELETE, "/api/v1/client/**").authenticated();

        http.authorizeRequests().anyRequest().permitAll();
        http.addFilter(customeAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                List.of("http://192.168.1.8:5173", "http://localhost:5173", "http://localhost:5174", "https://webhook.site", "https://webhook.site/df9d9c22-0473-4e6a-9cc3-d1d122e75936", "https://webhook.site/#!/df9d9c22-0473-4e6a-9cc3-d1d122e75936/"));
        configuration.setAllowedMethods(List.of("HEAD",
                "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("*"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
