package com.zepinos.realty.service.admin;

import com.zepinos.realty.dto.RealtyUserDetails;
import com.zepinos.realty.dto.group.GroupGet;
import com.zepinos.realty.dto.user.UserGet;
import com.zepinos.realty.jooq.tables.pojos.Groups;
import com.zepinos.realty.jooq.tables.records.UsersRecord;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.UpdateSetMoreStep;
import org.jooq.tools.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

import static com.zepinos.realty.jooq.tables.Authorities.AUTHORITIES;
import static com.zepinos.realty.jooq.tables.GroupUsers.GROUP_USERS;
import static com.zepinos.realty.jooq.tables.Groups.GROUPS;
import static com.zepinos.realty.jooq.tables.Users.USERS;
import static org.jooq.impl.DSL.countDistinct;
import static org.jooq.impl.DSL.when;

@Service
public class UserService {

    private final DSLContext dsl;
    private final GroupService groupService;

    public UserService(DSLContext dsl, GroupService groupService) {
        this.dsl = dsl;
        this.groupService = groupService;
    }

    @Transactional
    public Map<String, Object> post(UserGet userGet, int groupSeq, RealtyUserDetails realtyUserDetails) throws Exception {

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

        int cnt = dsl
                .selectCount()
                .from(USERS)
                .where(USERS.USERNAME.eq(userGet.getUsername()))
                .fetchOne(0, int.class);

        if (cnt > 0)
            return Map.of("status", 1005, "message", "이미 사용중인 아이디입니다.");

        // users 테이블 저장
        UsersRecord userRecord = dsl.newRecord(USERS, userGet);
        userRecord.setEnabled("1");
        userRecord.setUserAdd(realtyUserDetails.getUserSeq());
        userRecord.setDateAdd(LocalDateTime.now());
        userRecord.setUserMod(realtyUserDetails.getUserSeq());
        userRecord.setDateMod(LocalDateTime.now());

        cnt = userRecord.store();

        if (cnt < 1)
            throw new RuntimeException("[1002-001] 사용자 등록에 실패하였습니다.");

        int userSeq = userRecord.getUserSeq();
        String username = userRecord.getUsername();

        cnt = dsl
                .insertInto(AUTHORITIES)
                .columns(AUTHORITIES.USER_SEQ, AUTHORITIES.USERNAME, AUTHORITIES.AUTHORITY)
                .values(userSeq, username, "ROLE_USER")
                .execute();

        if (cnt < 1)
            throw new RuntimeException("[1002-002] 사용자 등록에 실패하였습니다.");

        // group_users 테이블 저장
        cnt = dsl
                .insertInto(GROUP_USERS)
                .columns(GROUP_USERS.GROUP_SEQ, GROUP_USERS.USER_SEQ, GROUP_USERS.USERNAME)
                .values(groupSeq, userSeq, username)
                .execute();

        if (cnt < 1)
            throw new RuntimeException("[1002-003] 사용자 등록에 실패하였습니다.");

        return Map.of("status", 0, "count", cnt, "userSeq", userSeq, "groupSeq", groupSeq);

    }

    public UserGet getUser(int userSeq) {

        UserGet userGet = dsl
                .select(USERS.USER_SEQ,
                        USERS.USERNAME,
                        USERS.USER_REAL_NAME,
                        when(USERS.ENABLED.eq("1"), "정상").
                                otherwise("중지").as("enabled"))
                .from(USERS)
                .where(USERS.USER_SEQ.eq(userSeq))
                .fetchOneInto(UserGet.class);

        return userGet;

    }

    /**
     * 사용자 일련번호로 그룹 정보 가져오기
     *
     * @param userSeq
     * @return
     */
    public GroupGet getGroup(int userSeq) {

        Integer groupSeq = dsl
                .select(GROUP_USERS.GROUP_SEQ)
                .from(GROUP_USERS)
                .where(GROUP_USERS.USER_SEQ.eq(userSeq))
                .fetchOne(0, int.class);

        return groupService.getGroup(groupSeq);

    }

    @Transactional
    public Map<String, Object> put(UserGet userGet, RealtyUserDetails realtyUserDetails) throws Exception {

        int userSeq = userGet.getUserSeq();

        // 그룹 사용자 수 제한, 사용기한 확인 확인
        Groups groups = getGroup(userSeq);

        int groupSeq = groups.getGroupSeq();

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

        if (groups.getMaxUsers() < currentUsers.value1())
            return Map.of("status", 1004, "message", "허용 인원이 초과되었습니다.");

        int cnt = dsl
                .selectCount()
                .from(USERS)
                .where(USERS.USERNAME.eq(userGet.getUsername()))
                .and(USERS.USER_SEQ.ne(userSeq))
                .fetchOne(0, int.class);

        if (cnt > 0)
            return Map.of("status", 1005, "message", "이미 사용중인 아이디입니다.");

        // users 테이블 저장
        UpdateSetMoreStep<UsersRecord> userUpdate = dsl
                .update(USERS)
                .set(USERS.USERNAME, userGet.getUsername())
                .set(USERS.USER_REAL_NAME, userGet.getUserRealName())
                .set(USERS.USER_MOD, realtyUserDetails.getUserSeq())
                .set(USERS.DATE_MOD, LocalDateTime.now());

        if (!StringUtils.isEmpty(userGet.getPassword()))
            userUpdate.set(USERS.PASSWORD, userGet.getPassword());

        cnt = userUpdate
                .where(USERS.USER_SEQ.eq(userSeq))
                .execute();

        if (cnt < 1)
            throw new RuntimeException("[1002-004] 사용자 수정에 실패하였습니다.");

        return Map.of("status", 0, "count", cnt, "userSeq", userSeq, "groupSeq", groupSeq);

    }

    @Transactional
    public Map<String, Object> delete(int userSeq, RealtyUserDetails realtyUserDetails) throws Exception {

        int cnt = dsl
                .update(USERS)
                .set(USERS.ENABLED, "0")
                .set(USERS.USER_MOD, realtyUserDetails.getUserSeq())
                .set(USERS.DATE_MOD, LocalDateTime.now())
                .where(USERS.USER_SEQ.eq(userSeq))
                .execute();

        if (cnt < 1)
            throw new RuntimeException("[1002-005] 사용자 비활성화에 실패하였습니다.");

        int groupSeq = getGroup(userSeq).getGroupSeq();

        return Map.of("status", 0, "count", cnt, "userSeq", userSeq, "groupSeq", groupSeq);

    }

}
