package com.zepinos.realty.dto.admin;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupList {

    private int groupSeq;
    private String groupName;
    private String groupAdmin;
    private LocalDateTime expireDatetime;
    private int maxUsers;
    private int currentUsers;

}
