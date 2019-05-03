package com.zepinos.realty.controller.admin;

import com.zepinos.realty.dto.SearchDto;
import com.zepinos.realty.dto.admin.GroupGet;
import com.zepinos.realty.service.admin.GroupService;
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
@RequestMapping("/admin/group")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("")
    public String list(ModelMap modelMap) {
        return "admin/group/list";
    }

    @GetMapping("/{groupSeq}")
    public String get(ModelMap modelMap,
                      @PathVariable int groupSeq) {

        GroupGet content = null;
        try {

            content = groupService.getGroup(groupSeq);

        } catch (Exception e) {

            e.printStackTrace();

        }

        modelMap.put("content", content);

        return "admin/group/content";

    }

    @GetMapping("/{groupSeq}/edit")
    public String edit(ModelMap modelMap,
                       @PathVariable int groupSeq) {

        GroupGet content = null;
        try {

            content = groupService.getGroup(groupSeq);

        } catch (Exception e) {

            e.printStackTrace();

        }

        modelMap.put("content", content);

        return "admin/group/edit";

    }

    @GetMapping("/ajax/{groupSeq}")
    @ResponseBody
    public Callable<Map<String, Object>> ajaxGroupUsers(@ModelAttribute SearchDto searchDto,
                                                        @PathVariable int groupSeq) {

        return () -> {

            Map<String, Object> result = null;
            try {

                log.info("searchDto : {}", searchDto);

                result = groupService.ajaxGroupUsers(searchDto, groupSeq);

            } catch (Exception e) {

                e.printStackTrace();

            }

            return result;

        };

    }

    @PostMapping("/ajax/list")
    @ResponseBody
    public Callable<Map<String, Object>> ajaxGroupList(@ModelAttribute SearchDto searchDto) {

        return () -> {

            Map<String, Object> result = null;
            try {

                log.info("searchDto : {}", searchDto);

                result = groupService.ajaxGroupList(searchDto);

            } catch (Exception e) {

                e.printStackTrace();

            }

            return result;

        };

    }

    @PutMapping("/{groupSeq}")
    @ResponseBody
    public Callable<Map<String, Object>> put(@Valid @ModelAttribute GroupGet groupGet,
                                             BindingResult bindingResult) {

        return () -> {

            if (bindingResult.hasErrors())
                return Map.of("status", 1001, "message", "매개변수 오류", "field_errors", bindingResult.getFieldErrors());

            Map<String, Object> result = null;
            try {

                result = groupService.putGroup(groupGet);

            } catch (Exception e) {

                e.printStackTrace();

            }

            return result;

        };

    }

}
