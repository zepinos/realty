package com.zepinos.realty.dto;

import lombok.Data;

import java.util.Map;

@Data
public class SearchDto {

    private int draw;
    private int start;
    private int length;

    private Map<String, String> search;

}
