package com.zepinos.realty.service.admin;

import com.zepinos.realty.dto.RealtyUserDetails;
import com.zepinos.realty.jooq.tables.pojos.Groups;
import com.zepinos.realty.jooq.tables.pojos.Users;
import com.zepinos.realty.jooq.tables.records.GroupUsersRecord;
import com.zepinos.realty.jooq.tables.records.UsersRecord;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

import static com.zepinos.realty.jooq.tables.GroupUsers.GROUP_USERS;
import static com.zepinos.realty.jooq.tables.Groups.GROUPS;
import static com.zepinos.realty.jooq.tables.Users.USERS;
import static org.jooq.impl.DSL.countDistinct;

@Service
public class UserService {

    private final DSLContext dsl;

    public UserService(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional
    public Map<String, Object> post(Users users, int groupSeq, RealtyUserDetails realtyUserDetails) throws Exception {

        // 그룹 사용자 수 제한, 사용기한 확인 확인
        Groups groups = dsl
                .selectFrom(GROUPS)
                .where(GROUPS.GROUP_SEQ.eq(groupSeq))
                .fetchOneInto(Groups.class);

        if (LocalDateTime.now().isAfter(groups.getExpireDatetime()))
            return Map.of("status", 1003, "message", "사용기간이 만료되었습니다.");

        // group 내 활성화 사용자 수 조회
        Record1<Integer> currentUsers = dsl
                .select(countDistinct(USERS.USER_SEQ))
                .from(USERS)
                .join(GROUP_USERS)
                .on(GROUP_USERS.USER_SEQ.eq(USERS.USER_SEQ))
                .and(GROUP_USERS.GROUP_SEQ.eq(groupSeq))
                .where(USERS.ENABLED.eq("1"))
                .fetchOne();

        if (groups.getMaxUsers() <= currentUsers.value1())
            return Map.of("status", 1004, "message", "허용 인원이 초과되었습니다.");

        // users 테이블 저장
        UsersRecord userRecord = dsl.newRecord(USERS, users);
        userRecord.setEnabled("1");
        userRecord.setUserAdd(realtyUserDetails.getUserSeq());
        userRecord.setDateAdd(LocalDateTime.now());
        userRecord.setUserMod(realtyUserDetails.getUserSeq());
        userRecord.setDateMod(LocalDateTime.now());

        int cnt = userRecord.store();

        if (cnt < 1)
            return Map.of("status", 1002, "message", "사용자 등록에 실패하였습니다.");

        int userSeq = userRecord.getUserSeq();

        // group_users 테이블 저장
        cnt = dsl
                .insertInto(GROUP_USERS)
                .columns(GROUP_USERS.GROUP_SEQ, GROUP_USERS.USER_SEQ, GROUP_USERS.USERNAME)
                .values(groupSeq, userSeq, userRecord.getUsername())
                .execute();

        return Map.of("status", 0, "count", cnt, "userSeq", userSeq, "groupSeq", groupSeq);

    }

}
