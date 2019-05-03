package com.zepinos.realty.dto.admin;

import com.zepinos.realty.jooq.tables.pojos.Groups;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@EqualsAndHashCode(callSuper = false)
public class GroupGet extends Groups {

    private int groupAdminSeq;
    private String groupAdmin;
    private String expireDatetimeString;
    private int currentUsers;

    public String getExpireDatetimeString() {
        return getExpireDatetime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public void setExpireDatetimeString(String expireDatetimeString) {
        setExpireDatetime(LocalDateTime.parse(expireDatetimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

}
