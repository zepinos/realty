package com.zepinos.realty.service;

import com.zepinos.realty.dto.SearchDto;
import com.zepinos.realty.dto.admin.GroupList;
import com.zepinos.realty.jooq.tables.Authorities;
import com.zepinos.realty.jooq.tables.GroupUsers;
import com.zepinos.realty.jooq.tables.Groups;
import com.zepinos.realty.jooq.tables.Users;
import org.jooq.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.zepinos.realty.jooq.tables.Authorities.AUTHORITIES;
import static com.zepinos.realty.jooq.tables.GroupUsers.GROUP_USERS;
import static com.zepinos.realty.jooq.tables.Groups.GROUPS;
import static com.zepinos.realty.jooq.tables.Users.USERS;
import static org.jooq.impl.DSL.*;

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

        Field<Object> GROUP_ADMIN = dsl
                .select(concat(Y.USER_REAL_NAME, val("("), Y.USERNAME, val(")")))
                .from(X)
                .join(Y)
                .on(Y.USER_SEQ.eq(X.USER_SEQ))
                .join(Z)
                .on(Z.USER_SEQ.eq(Y.USER_SEQ))
                .and(Z.AUTHORITY.eq("ROLE_GROUP"))
                .where(X.GROUP_SEQ.eq(A.GROUP_SEQ))
                .asField("group_admin");

        Field<Object> CURRENT_USERS = dsl
                .selectCount()
                .from(GROUP_USERS)
                .join(USERS)
                .on(USERS.USER_SEQ.eq(GROUP_USERS.USER_SEQ))
                .where(USERS.ENABLED.eq("1"))
                .and(GROUP_USERS.GROUP_SEQ.eq(A.GROUP_SEQ))
                .asField("currentUsers");

        var R = dsl
                .select(
                        A.GROUP_SEQ,
                        A.GROUP_NAME,
                        GROUP_ADMIN,
                        A.EXPIRE_DATETIME,
                        A.MAX_USERS,
                        CURRENT_USERS
                )
                .from(A)
                .orderBy(A.GROUP_NAME)
                .asTable("R");

        int recordsTotal = dsl
                .selectCount()
                .from(R)
                .fetchOne(0, int.class);

        var search = searchDto.getSearch();
        var searchValue = search.get("value");

        int recordsFiltered = dsl
                .selectCount()
                .from(R)
                .where(field("group_name").like(concat(val("%"), val(searchValue), val("%"))))
                .or(field("group_admin").like(concat(val("%"), val(searchValue), val("%"))))
                .fetchOne(0, int.class);

        List<GroupList> groupLists = dsl
                .select()
                .from(R)
                .where(field("group_name").like(concat(val("%"), val(searchValue), val("%"))))
                .or(field("group_admin").like(concat(val("%"), val(searchValue), val("%"))))
                .limit(searchDto.getLength())
                .offset(searchDto.getStart())
                .fetchInto(GroupList.class);

        return Map.of("status", 0, "draw", searchDto.getDraw(), "recordsTotal", recordsTotal, "recordsFiltered", recordsFiltered, "data", groupLists);

    }

}
