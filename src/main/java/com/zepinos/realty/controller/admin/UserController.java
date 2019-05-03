package com.zepinos.realty.controller.admin;

import com.zepinos.realty.dto.admin.GroupGet;
import com.zepinos.realty.service.admin.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.Callable;

@Slf4j
@Controller
@RequestMapping("/admin/user")
public class UserController {

    private final GroupService groupService;

    public UserController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/new/group/{groupSeq}")
    public Callable<String> newUser(@PathVariable int groupSeq,
                                    ModelMap modelMap) {

        return () -> {

            try {

                // 사용자 등록할 그룹의 정보 조회
                GroupGet group = groupService.getGroup(groupSeq);

                modelMap.put("group", group);

            } catch (Exception e) {

                e.printStackTrace();

            }

            return "admin/user/new";

        };

    }

}
