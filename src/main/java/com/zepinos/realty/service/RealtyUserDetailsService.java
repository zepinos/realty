package com.zepinos.realty.service;

import com.zepinos.realty.jooq.tables.pojos.Authorities;
import com.zepinos.realty.jooq.tables.pojos.Users;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.zepinos.realty.jooq.tables.Authorities.AUTHORITIES;
import static com.zepinos.realty.jooq.tables.Users.USERS;

@Service
public class RealtyUserDetailsService implements UserDetailsService {

    @Autowired
    private DSLContext dsl;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users users = dsl
                .selectFrom(USERS)
                .where(USERS.USERNAME.eq(username))
                .and(USERS.ENABLED.eq("1"))
                .fetchOneInto(Users.class);

        if (users == null || users.getUserSeq() == null) throw new UsernameNotFoundException(username);

        List<Authorities> authoritiesList = dsl
                .select(AUTHORITIES.AUTHORITY)
                .from(AUTHORITIES)
                .where(AUTHORITIES.USER_SEQ.eq(users.getUserSeq()))
                .fetchInto(Authorities.class);

        return new RealtyUserDetails(users, authoritiesList);

    }

}

@Slf4j
class RealtyUserDetails implements UserDetails {

    private Users users;
    private List<Authorities> authoritiesList;

//    public RealtyUserDetails(Users users) {
//        this.users = users;
//    }

    public RealtyUserDetails(Users users, List<Authorities> authoritiesList) {
        this.users = users;
        this.authoritiesList = authoritiesList;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> auth = new ArrayList<>();
        authoritiesList.forEach(authorities -> auth.add(new SimpleGrantedAuthority(authorities.getAuthority())));

        return auth;

    }

    @Override
    public String getUsername() {
        return users.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return StringUtils.equals(users.getEnabled(), "1") ? true : false;
    }

    @Override
    public String getPassword() {
        return users.getPassword();
    }

    public String getUserRealName() {
        return users.getUserRealName();
    }

}
