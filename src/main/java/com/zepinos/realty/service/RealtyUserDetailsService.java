package com.zepinos.realty.service;

import com.zepinos.realty.dto.RealtyUserDetails;
import com.zepinos.realty.jooq.tables.pojos.Authorities;
import com.zepinos.realty.jooq.tables.pojos.Groups;
import com.zepinos.realty.jooq.tables.pojos.Users;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.zepinos.realty.jooq.tables.Authorities.AUTHORITIES;
import static com.zepinos.realty.jooq.tables.GroupUsers.GROUP_USERS;
import static com.zepinos.realty.jooq.tables.Groups.GROUPS;
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

        if (users == null || users.getUserSeq() == null)
            throw new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다.");

        int userSeq = users.getUserSeq();

        int adminCnt = dsl
                .selectCount()
                .from(AUTHORITIES)
                .where(AUTHORITIES.USER_SEQ.eq(userSeq))
                .and(AUTHORITIES.AUTHORITY.eq("ROLE_ADMIN"))
                .fetchOne(0, int.class);

        Groups groups = dsl
                .select(GROUPS.asterisk())
                .from(GROUPS)
                .join(GROUP_USERS)
                .on(GROUPS.GROUP_SEQ.eq(GROUP_USERS.GROUP_SEQ))
                .where(GROUP_USERS.USER_SEQ.eq(users.getUserSeq()))
                .fetchOneInto(Groups.class);

        if (adminCnt < 1) {

            if (groups == null || groups.getGroupSeq() < 1)
                throw new AuthenticationCredentialsNotFoundException("그룹 정보를 찾을 수 없습니다.");
            else if (LocalDateTime.now().isAfter(groups.getExpireDatetime()))
                throw new AccountExpiredException("그룹 사용기간이 만료되었습니다. 기간연장신청을 진행해주세요.");

        }

        List<Authorities> authoritiesList = dsl
                .select(AUTHORITIES.AUTHORITY)
                .from(AUTHORITIES)
                .where(AUTHORITIES.USER_SEQ.eq(users.getUserSeq()))
                .fetchInto(Authorities.class);

        return new RealtyUserDetails(users, groups, authoritiesList);

    }

}
