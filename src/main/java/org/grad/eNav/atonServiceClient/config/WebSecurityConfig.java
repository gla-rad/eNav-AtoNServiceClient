/*
 * Copyright (c) 2022 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grad.eNav.atonServiceClient.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The Web Security Configuration.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * The HTTP security configuration.
     * <p>
     * For now this will allow all requests to the micro-service web, health
     * and login endpoints without any authorisation requirements.
     *
     * @param httpSecurity The HTTP security
     * @throws Exception Exception thrown while configuring the security
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers(
                            "/webjars/**",   //bootstrap
                            "/css/**",          //css files
                            "/lib/**",          //js files
                            "/images/**",       //the images
                            "/src/**",          //the javascript sources
                            "/api/secom/**",    //the SECOM interfaces
                            "/", "/index.html"  // The main index page
                    ).permitAll()
                    .antMatchers(HttpMethod.POST, "/api/secom/v1/upload").permitAll()
                    .anyRequest().permitAll()
                    .and()
                .formLogin()
                    .permitAll()
                    .and()
                .logout()
                    .permitAll()
                    .and()
                .httpBasic();
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("user")
//                .password(encoder().encode("password"))
//                .roles("USER");
//    }
//
//    @Bean
//    public PasswordEncoder encoder() {
//        return new BCryptPasswordEncoder();
//    }

//    public class CsrfTokenResponseHeaderBindingFilter extends OncePerRequestFilter {
//        protected static final String REQUEST_ATTRIBUTE_NAME = "_csrf";
//        protected static final String RESPONSE_HEADER_NAME = "X-CSRF-HEADER";
//        protected static final String RESPONSE_PARAM_NAME = "X-CSRF-PARAM";
//        protected static final String RESPONSE_TOKEN_NAME = "X-CSRF-TOKEN";
//
//        @Override
//        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, javax.servlet.FilterChain filterChain) throws ServletException, IOException, ServletException, IOException {
//            CsrfToken token = (CsrfToken) request.getAttribute(REQUEST_ATTRIBUTE_NAME);
//
//            if (token != null) {
//                response.setHeader(RESPONSE_HEADER_NAME, token.getHeaderName());
//                response.setHeader(RESPONSE_PARAM_NAME, token.getParameterName());
//                response.setHeader(RESPONSE_TOKEN_NAME, token.getToken());
//            }
//
//            filterChain.doFilter(request, response);
//
//
//        }
//    }
}
