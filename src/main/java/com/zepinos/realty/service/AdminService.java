package com.zepinos.realty.service;

import com.zepinos.realty.dto.SearchDto;
import com.zepinos.realty.jooq.tables.Authorities;
import com.zepinos.realty.jooq.tables.GroupUsers;
import com.zepinos.realty.jooq.tables.Groups;
import com.zepinos.realty.jooq.tables.Users;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.zepinos.realty.jooq.tables.Authorities.AUTHORITIES;
import static com.zepinos.realty.jooq.tables.GroupUsers.GROUP_USERS;
import static com.zepinos.realty.jooq.tables.Groups.GROUPS;
import static com.zepinos.realty.jooq.tables.Users.USERS;
import static org.jooq.impl.DSL.concat;
import static org.jooq.impl.DSL.val;

@Service
public class AdminService {

    private final DSLContext dsl;

    public AdminService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Map<String, Object> ajaxGroupList(SearchDto searchDto) throws Exception {

        // groups, group_users 테이블 조회
        GroupUsers X = GROUP_USERS.as("X");
        Users Y = USERS.as("Y");
        Authorities Z = AUTHORITIES.as("Z");
        Groups A = GROUPS.as("A");

        Field<Object> USER_REAL_NAME = dsl
                .select(concat(Y.USER_REAL_NAME, val("("), Y.USERNAME, val(")")))
                .from(X)
                .join(Y)
                .on(Y.USER_SEQ.eq(X.USER_SEQ))
                .join(Z)
                .on(Z.USER_SEQ.eq(Y.USER_SEQ))
                .and(Z.AUTHORITY.eq("ROLE_GROUP"))
                .asField("user_real_name");

        List<Map<String, Object>> groupList = dsl.
                select(
                        A.GROUP_NAME,
                        USER_REAL_NAME,
                        A.EXPIRE_DATETIME,
                        A.MAX_USERS
                )
                .from(A)
                .fetchMaps();

        return Map.of("status", 0, "draw", searchDto.getDraw(), "recordsTotal", 3, "recordsFiltered", 3, "data", groupList);

    }

}
