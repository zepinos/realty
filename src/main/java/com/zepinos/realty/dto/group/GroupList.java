package com.zepinos.realty.dto.group;

import com.zepinos.realty.jooq.tables.pojos.Groups;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.format.DateTimeFormatter;

@Data
@EqualsAndHashCode(callSuper = false)
public class GroupList extends Groups {

    private String groupAdmin;
    private String expireDatetimeString;
    private int currentUsers;

    public String getExpireDatetimeString() {
        return getExpireDatetime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

}
