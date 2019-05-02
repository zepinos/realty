package com.zepinos.realty.controller;

import com.zepinos.realty.dto.SearchDto;
import com.zepinos.realty.dto.admin.GroupGet;
import com.zepinos.realty.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/group")
    public String list(ModelMap modelMap) {
        return "admin/group/list";
    }

    @GetMapping("/group/{groupSeq}")
    public String get(ModelMap modelMap,
                      @PathVariable int groupSeq) {

        GroupGet content = null;
        try {

            content = adminService.getGroup(groupSeq);

        } catch (Exception e) {

            e.printStackTrace();

        }

        modelMap.put("content", content);

        return "admin/group/content";

    }

    @GetMapping("/group/{groupSeq}/edit")
    public String edit(ModelMap modelMap,
                       @PathVariable int groupSeq) {

        GroupGet content = null;
        try {

            content = adminService.getGroup(groupSeq);

        } catch (Exception e) {

            e.printStackTrace();

        }

        modelMap.put("content", content);

        return "admin/group/edit";

    }

    @GetMapping("/ajax/group/{groupSeq}")
    @ResponseBody
    public Callable<Map<String, Object>> ajaxGroupUsers(@ModelAttribute SearchDto searchDto,
                                                        @PathVariable int groupSeq) {

        return () -> {

            Map<String, Object> result = null;
            try {

                log.info("searchDto : {}", searchDto);

                result = adminService.ajaxGroupUsers(searchDto, groupSeq);

            } catch (Exception e) {

                e.printStackTrace();

            }

            return result;

        };

    }

    @PostMapping("/ajax/group/list")
    @ResponseBody
    public Callable<Map<String, Object>> ajaxGroupList(@ModelAttribute SearchDto searchDto) {

        return () -> {

            Map<String, Object> result = null;
            try {

                log.info("searchDto : {}", searchDto);

                result = adminService.ajaxGroupList(searchDto);

            } catch (Exception e) {

                e.printStackTrace();

            }

            return result;

        };

    }

    @PutMapping("/group/{groupSeq}")
    @ResponseBody
    public Callable<Map<String, Object>> put(@Valid @ModelAttribute GroupGet groupGet,
                                             BindingResult bindingResult) {

        return () -> {

            if (bindingResult.hasErrors())
                return Map.of("status", 1001, "message", "매개변수 오류", "field_errors", bindingResult.getFieldErrors());

            Map<String, Object> result = null;
            try {

                result = adminService.putGroup(groupGet);

            } catch (Exception e) {

                e.printStackTrace();

            }

            return result;

        };

    }

}
