package com.zepinos.realty.controller.admin;

import com.zepinos.realty.dto.RealtyUserDetails;
import com.zepinos.realty.dto.group.GroupGet;
import com.zepinos.realty.jooq.tables.pojos.Users;
import com.zepinos.realty.service.admin.GroupService;
import com.zepinos.realty.service.admin.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@Controller
@RequestMapping("/admin/user")
public class UserController {

    private final GroupService groupService;
    private final UserService userService;

    public UserController(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
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

    @PostMapping("")
    @ResponseBody
    public Callable<Map<String, Object>> post(@Valid @ModelAttribute Users users,
                                              BindingResult bindingResult,
                                              @RequestParam int groupSeq,
                                              @AuthenticationPrincipal RealtyUserDetails realtyUserDetails) {

        return () -> {

            if (bindingResult.hasErrors())
                return Map.of("status", 1001, "message", "매개변수 오류", "field_errors", bindingResult.getFieldErrors());

            Map<String, Object> result = null;
            try {

                result = userService.post(users, groupSeq, realtyUserDetails);

            } catch (Exception e) {

                e.printStackTrace();

            }

            return result;

        };

    }

}
