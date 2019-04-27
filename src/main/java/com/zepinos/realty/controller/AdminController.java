package com.zepinos.realty.controller;

import com.zepinos.realty.dto.SearchDto;
import com.zepinos.realty.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

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

}
