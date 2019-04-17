package com.zepinos.realty.controller;

import com.zepinos.realty.jooq.tables.pojos.RealtyList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/realty")
public class RealtyController {

    @GetMapping("/new")
    public String newRealty() {
        return "realty/new";
    }

    @PostMapping("")
    @ResponseBody
    public Map<String, Object> post(@ModelAttribute RealtyList realtyList) {

        Map<String, Object> result = new HashMap<>();
        result.put("status", 0);

        log.info("realtyList : {}", realtyList);

        return result;

    }

}
