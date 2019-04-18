package com.zepinos.realty.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

//        http.httpBasic().disable();

        http.cors().and().csrf().disable();

        http.authorizeRequests()
                .antMatchers("/css/**", "/js/**", "/img/**").permitAll()
//                .antMatchers("/admin/**").hasRole("ROLE_ADMIN")
                .antMatchers("/realty/**").permitAll()
                .anyRequest().authenticated();

//        http.formLogin()
//                .loginPage("/login") // default
//                .loginProcessingUrl("/authenticate")
//                .failureUrl("/login?error") // default
//                .defaultSuccessUrl("/")
//                .usernameParameter("email")
//                .passwordParameter("password")
//                .permitAll();
//
//        http.logout()
//                .logoutUrl("/logout") // default
//                .logoutSuccessUrl("/login")
//                .permitAll();

    }

}
