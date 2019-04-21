package com.zepinos.realty.service;

import com.zepinos.realty.jooq.enums.RealtyListRealtyStatus;
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
        List<RealtyList> realtyList = dsl.
                selectFrom(REALTY_LIST)
                .where(REALTY_LIST.REALTY_STATUS.eq(RealtyListRealtyStatus.USE))
                .orderBy(REALTY_LIST.REALTY_SEQ.desc())
                .fetchInto(RealtyList.class);

        return realtyList;

    }

    public RealtyList get(long realtySeq) throws Exception {

        // realty_list 테이블 조회
        RealtyList realtyList = dsl
                .selectFrom(REALTY_LIST)
                .where(REALTY_LIST.REALTY_SEQ.eq(realtySeq)
                        .and(REALTY_LIST.REALTY_STATUS.eq(RealtyListRealtyStatus.USE)))
                .fetchOneInto(RealtyList.class);

        return realtyList;

    }

    public Map<String, Object> post(RealtyList realtyList) throws Exception {

        // realty_list 테이블 저장
        RealtyListRecord realtyListRecord = dsl.newRecord(REALTY_LIST, realtyList);
        realtyListRecord.setDateAdd(LocalDateTime.now());
        realtyListRecord.setDateMod(LocalDateTime.now());

        int cnt = realtyListRecord.store();

        return Map.of("status", 0, "count", cnt, "realtySeq", realtyListRecord.getRealtySeq());

    }

    public Map<String, Object> delete(long realtySeq) throws Exception {

        // realty_list 테이블에 상태 저장
        int cnt = dsl
                .update(REALTY_LIST)
                .set(REALTY_LIST.REALTY_STATUS, RealtyListRealtyStatus.HIDDEN)
                .where(REALTY_LIST.REALTY_SEQ.eq(realtySeq))
                .execute();

        return Map.of("status", 0, "count", cnt);

    }

    public Map<String, Object> ajaxList(int draw) throws Exception {

        // realty_list 테이블 조회
        List<RealtyList> realtyList = dsl
                .select(REALTY_LIST.REALTY_SEQ,
                        REALTY_LIST.REALTY_NAME,
                        REALTY_LIST.ADDRESS,
                        REALTY_LIST.BNAME)
                .from(REALTY_LIST)
                .where(REALTY_LIST.REALTY_STATUS.eq(RealtyListRealtyStatus.USE))
                .orderBy(REALTY_LIST.REALTY_SEQ.desc())
                .fetchInto(RealtyList.class);

        return Map.of("status", 0, "draw", draw, "recordsTotal", realtyList.size(), "recordsFiltered", realtyList.size(
        ), "data", realtyList);

    }

    public Map<String, Object> ajaxReadRealty(long realtySeq,
                                              double swLat, double swLng, double neLat, double neLng) throws Exception {

        // 주변 물건 조회
        List<RealtyList> realtyList = dsl
                .select(REALTY_LIST.REALTY_SEQ,
                        REALTY_LIST.REALTY_NAME,
                        REALTY_LIST.LAT,
                        REALTY_LIST.LNG)
                .from(REALTY_LIST)
                .where(REALTY_LIST.REALTY_SEQ.ne(realtySeq)
                        .and(REALTY_LIST.LAT.between(swLat, neLat))
                        .and(REALTY_LIST.LNG.between(swLng, neLng))
                )
                .orderBy(REALTY_LIST.REALTY_SEQ.desc())
                .fetchInto(RealtyList.class);

        return Map.of("status", 0, "list", realtyList);

    }

}
