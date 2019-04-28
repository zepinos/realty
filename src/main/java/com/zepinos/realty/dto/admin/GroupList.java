package com.zepinos.realty.dto.admin;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupList {

    private String groupName;
    private String groupAdmin;
    private LocalDateTime expireDateTime;
    private int maxUsers;
    private int currentUsers;

}
