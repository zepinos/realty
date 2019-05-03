package com.zepinos.realty.dto;

import com.zepinos.realty.jooq.tables.pojos.Authorities;
import com.zepinos.realty.jooq.tables.pojos.Groups;
import com.zepinos.realty.jooq.tables.pojos.Users;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jooq.tools.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@ToString
@EqualsAndHashCode
public class RealtyUserDetails implements UserDetails {

    private Users users;
    private Groups groups;
    private List<Authorities> authoritiesList;

//    public RealtyUserDetails(Users users) {
//        this.users = users;
//    }

    public RealtyUserDetails(Users users, Groups groups, List<Authorities> authoritiesList) {
        this.users = users;
        this.groups = groups;
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

    public int getUserSeq() {
        return users.getUserSeq();
    }

    public Integer getGroupSeq() {
        return groups == null ? null : groups.getGroupSeq();
    }

    public String getGroupName() {
        return groups == null ? null : groups.getGroupName();
    }

}

