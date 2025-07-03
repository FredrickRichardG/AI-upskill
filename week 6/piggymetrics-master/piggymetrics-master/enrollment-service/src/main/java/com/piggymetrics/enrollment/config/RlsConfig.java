package com.piggymetrics.enrollment.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.DelegatingDataSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class RlsConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public OncePerRequestFilter rlsFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                String userId = request.getHeader("X-User-Id");
                if (userId != null) {
                    try (Connection conn = dataSource.getConnection()) {
                        conn.createStatement().execute("SET app.user_id = '" + userId + "'");
                    } catch (SQLException e) {
                        throw new ServletException("Failed to set app.user_id", e);
                    }
                }
                filterChain.doFilter(request, response);
            }
        };
    }
} 