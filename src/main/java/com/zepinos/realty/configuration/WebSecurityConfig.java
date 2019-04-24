package com.zepinos.realty.configuration;

import com.zepinos.realty.service.RealtyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
//    }

    @Autowired
    private RealtyUserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());

        return daoAuthenticationProvider;

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

//        http.httpBasic().disable();

        http.cors().and().csrf().disable();

        http.authorizeRequests()
                .antMatchers("/css/**", "/js/**", "/img/**", "/vendor/**").permitAll()
                .antMatchers("/realty/**").hasAuthority("ROLE_USER")
                .antMatchers("/group/**").hasAuthority("ROLE_GROUP")
                .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated();

        http.formLogin()
//                .loginPage("/login") // default
//                .loginProcessingUrl("/authenticate")
//                .failureUrl("/login?error") // default
                .defaultSuccessUrl("/realty")
//                .usernameParameter("email")
//                .passwordParameter("password")
//                .permitAll()
        ;
//
//        http.logout()
//                .logoutUrl("/logout") // default
//                .logoutSuccessUrl("/login")
//                .permitAll();

    }

}
