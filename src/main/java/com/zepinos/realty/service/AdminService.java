package com.zepinos.realty.service;

import com.zepinos.realty.dto.SearchDto;
import com.zepinos.realty.dto.admin.GroupGet;
import com.zepinos.realty.dto.admin.GroupList;
import com.zepinos.realty.jooq.tables.Authorities;
import com.zepinos.realty.jooq.tables.GroupUsers;
import com.zepinos.realty.jooq.tables.Groups;
import com.zepinos.realty.jooq.tables.Users;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.springframework.stereotype.Service;

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

    public GroupGet getGroup(int groupSeq) throws Exception {

        // group 테이블 조회
        GroupGet groupGet = dsl
                .selectFrom(GROUPS)
                .where(GROUPS.GROUP_SEQ.eq(groupSeq))
                .fetchOneInto(GroupGet.class);

        // group 관리자 조회
        Record2<Integer, String> groupAdmin = dsl
                .select(USERS.USER_SEQ.as("group_seq"),
                        concat(USERS.USER_REAL_NAME, val("("), USERS.USERNAME, val(")")).as("group_admin"))
                .from(USERS)
                .join(AUTHORITIES)
                .on(AUTHORITIES.USER_SEQ.eq(USERS.USER_SEQ))
                .and(AUTHORITIES.AUTHORITY.eq("ROLE_GROUP"))
                .join(GROUP_USERS)
                .on(GROUP_USERS.USER_SEQ.eq(USERS.USER_SEQ))
                .and(GROUP_USERS.GROUP_SEQ.eq(groupSeq))
                .where(USERS.ENABLED.eq("1"))
                .limit(1)
                .fetchOne();

        if (groupAdmin != null) {

            groupGet.setGroupAdminSeq(groupAdmin.value1());
            groupGet.setGroupAdmin(groupAdmin.value2());

        }

        // group 내 활성화 사용자 수 조회
        Record1<Integer> currentUsers = dsl
                .select(countDistinct(USERS.USER_SEQ))
                .from(USERS)
                .join(AUTHORITIES)
                .on(AUTHORITIES.USER_SEQ.eq(USERS.USER_SEQ))
                .and(AUTHORITIES.AUTHORITY.in("ROLE_USER", "ROLE_GROUP"))
                .join(GROUP_USERS)
                .on(GROUP_USERS.USER_SEQ.eq(USERS.USER_SEQ))
                .and(GROUP_USERS.GROUP_SEQ.eq(groupSeq))
                .where(USERS.ENABLED.eq("1"))
                .fetchOne();

        if (currentUsers != null)
            groupGet.setCurrentUsers(currentUsers.value1());

        return groupGet;

    }

    public Map<String, Object> ajaxGroupUsers(SearchDto searchDto, int groupSeq) throws Exception {

        // users, group_users 테이블 조회
        List<com.zepinos.realty.jooq.tables.pojos.Users> users = dsl
                .selectDistinct(
                        USERS.USERNAME,
                        USERS.USER_REAL_NAME,
                        when(USERS.ENABLED.eq("1"), "정상").
                                otherwise("중지").as("enabled"))
                .from(USERS)
                .join(GROUP_USERS)
                .on(GROUP_USERS.USER_SEQ.eq(USERS.USER_SEQ))
                .and(GROUP_USERS.GROUP_SEQ.eq(groupSeq))
                .orderBy(USERS.ENABLED.desc(), USERS.USER_REAL_NAME)
                .fetchInto(com.zepinos.realty.jooq.tables.pojos.Users.class);

        return Map.of("status", 0, "draw", searchDto.getDraw(), "data", users);

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
                .select(countDistinct(USERS.USER_SEQ))
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
