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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * The Web Security Configuration.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    /**
     * The HTTP security configuration.
     * <p>
     * For now this will allow all requests to the micro-service web, health
     * and login endpoints without any authorisation requirements.
     *
     * @param httpSecurity The HTTP security
     * @throws Exception Exception thrown while configuring the security
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,
                        "/webjars/**",  //bootstrap
                        "/css/**",          //css files
                        "/lib/**",          //js files
                        "/images/**",       //the images
                        "/src/**",          //the javascript sources
                        "/", "/index.html"  // The main index page
                ).permitAll()
                .antMatchers(
                        "/api/secom/**",        //the SECOM interfaces
                        "/aton-service-client-websocket/**" //the web-socket
                ).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .and()
                .httpBasic(withDefaults());

        // Return the filter chain
        return httpSecurity.build();
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
