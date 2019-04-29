package com.zepinos.realty.controller;

import com.zepinos.realty.dto.SearchDto;
import com.zepinos.realty.jooq.tables.pojos.RealtyList;
import com.zepinos.realty.service.RealtyService;
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

    @GetMapping("")
    public String list(ModelMap modelMap) {
        return "realty/list";
    }

    @GetMapping("/{realtySeq}")
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
    public Callable<Map<String, Object>> post(@Valid @ModelAttribute RealtyList realtyList,
                                              BindingResult bindingResult) {

        return () -> {

            if (bindingResult.hasErrors())
                return Map.of("status", 1001, "message", "매개변수 오류", "field_errors", bindingResult.getFieldErrors());

            Map<String, Object> result = null;
            try {

                result = realtyService.post(realtyList);

            } catch (Exception e) {

                e.printStackTrace();

            }

            return result;

        };

    }

    @GetMapping("/{realtySeq}/edit")
    public String edit(ModelMap modelMap,
                       @PathVariable long realtySeq) {

        RealtyList content = null;
        try {

            content = realtyService.get(realtySeq);

        } catch (Exception e) {

            e.printStackTrace();

        }

        modelMap.put("content", content);

        return "realty/edit";

    }

    @PutMapping("/{realtySeq}")
    @ResponseBody
    public Callable<Map<String, Object>> put(@Valid @ModelAttribute RealtyList realtyList,
                                              BindingResult bindingResult) {

        return () -> {

            if (bindingResult.hasErrors())
                return Map.of("status", 1001, "message", "매개변수 오류", "field_errors", bindingResult.getFieldErrors());

            Map<String, Object> result = null;
            try {

                result = realtyService.put(realtyList);

            } catch (Exception e) {

                e.printStackTrace();

            }

            return result;

        };

    }

    @DeleteMapping("/{realtySeq}")
    @ResponseBody
    public Callable<Map<String, Object>> delete(@PathVariable long realtySeq) {

        return () -> {

            Map<String, Object> result = null;
            try {

                result = realtyService.delete(realtySeq);

            } catch (Exception e) {

                e.printStackTrace();

            }

            return result;

        };

    }

    @PostMapping("/ajax/list")
    @ResponseBody
    public Callable<Map<String, Object>> ajaxList(@ModelAttribute SearchDto searchDto) {

        return () -> {

            Map<String, Object> result = null;
            try {

                log.info("searchDto : {}", searchDto);

                result = realtyService.ajaxList(searchDto);

            } catch (Exception e) {

                e.printStackTrace();

            }

            return result;

        };

    }

    @PostMapping("/ajax/readRealty")
    @ResponseBody
    public Callable<Map<String, Object>> ajaxReadRealty(@RequestParam long realtySeq,
                                                        @RequestParam double swLat,
                                                        @RequestParam double swLng,
                                                        @RequestParam double neLat,
                                                        @RequestParam double neLng) {

        return () -> {

            Map<String, Object> result = null;
            try {

                result = realtyService.ajaxReadRealty(realtySeq, swLat, swLng, neLat, neLng);

            } catch (Exception e) {

                e.printStackTrace();

            }

            return result;

        };

    }

}
