package com.zepinos.realty.service;

import com.zepinos.realty.jooq.tables.pojos.RealtyList;
import com.zepinos.realty.jooq.tables.records.RealtyListRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.zepinos.realty.jooq.tables.RealtyList.REALTY_LIST;

@Service
public class RealtyService {

    private final DSLContext dsl;

    public RealtyService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<RealtyList> list() throws Exception {

        // realty_list 테이블 조회
        List<RealtyList> realtyList =
                dsl.selectFrom(REALTY_LIST)
                        .orderBy(REALTY_LIST.REALTY_SEQ.desc())
                        .fetchInto(RealtyList.class);

        return realtyList;

    }

    public RealtyList get(long realtySeq) throws Exception {

        // realty_list 테이블 조회
        RealtyList realtyList =
                dsl.selectFrom(REALTY_LIST)
                        .where(REALTY_LIST.REALTY_SEQ.eq(realtySeq))
                        .fetchOneInto(RealtyList.class);

        return realtyList;

    }

    public Map<String, Object> post(RealtyList realtyList) throws Exception {

        // realty_list 테이블 저장
        RealtyListRecord realtyListRecord = dsl.newRecord(REALTY_LIST, realtyList);
        realtyListRecord.setDateAdd(LocalDateTime.now());
        realtyListRecord.setDateMod(LocalDateTime.now());

        int cnt = realtyListRecord.store();

        return Map.of("status", 0, "count", cnt);

    }

}
