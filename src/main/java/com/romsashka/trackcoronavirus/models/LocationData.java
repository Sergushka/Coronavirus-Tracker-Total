package com.romsashka.trackcoronavirus.models;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
public class LocationData {
    private List<String> states;
    private String country;
    private long totalCases;
    private long differenceWithYesterday;
}
