package com.zepinos.realty.controller;

import com.zepinos.realty.jooq.tables.pojos.RealtyList;
import com.zepinos.realty.service.RealtyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@Controller
@RequestMapping("/realty")
public class RealtyController {

    private final RealtyService realtyService;

    public RealtyController(RealtyService realtyService) {
        this.realtyService = realtyService;
    }

    @GetMapping("/new")
    public String newRealty() {
        return "realty/new";
    }

    @GetMapping("/")
    public String list(ModelMap modelMap) {

        List<RealtyList> list = null;
        try {

            list = realtyService.list();

        } catch (Exception e) {

            e.printStackTrace();

        }

        modelMap.put("list", list);

        return "realty/list";

    }

    @GetMapping("/{realtySeq}/")
    public String get(ModelMap modelMap,
                      @PathVariable long realtySeq) {

        RealtyList content = null;
        try {

            content = realtyService.get(realtySeq);

        } catch (Exception e) {

            e.printStackTrace();

        }

        modelMap.put("content", content);

        return "realty/content";

    }

    @PostMapping("")
    @ResponseBody
    public Callable<Map<String, Object>> post(@ModelAttribute RealtyList realtyList) {

        return () -> {

            Map<String, Object> result = null;
            try {

                result = realtyService.post(realtyList);

            } catch (Exception e) {

                e.printStackTrace();

            }

            return result;

        };

    }

}
