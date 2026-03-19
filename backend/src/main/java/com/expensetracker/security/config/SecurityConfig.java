package com.expensetracker.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Removed SecurityFilterChain bean to avoid conflict with WebSecurityConfigurerAdapter

}
