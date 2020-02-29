package com.romsashka.trackcoronavirus.controllers;

import com.romsashka.trackcoronavirus.models.LocationData;
import com.romsashka.trackcoronavirus.services.CoronavirusDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HTMLController {

    private final CoronavirusDataService coronavirusDataService;

    public HTMLController(CoronavirusDataService coronavirusDataService) {
        this.coronavirusDataService = coronavirusDataService;
    }

    @GetMapping("/")
    public String html(Model model) {
        List<LocationData> locationData = coronavirusDataService.getLocationsDataSortedByTotalCases();
        long casesReportedToday = locationData.stream().mapToLong(LocationData::getTotalCases).sum();
        long differenceWithYesterday = locationData.stream().mapToLong(LocationData::getDifferenceWithYesterday).sum();
        model.addAttribute("locationData", locationData);
        model.addAttribute("casesReportedToday", casesReportedToday);
        model.addAttribute("differenceWithYesterday", differenceWithYesterday);
        return "index";
    }
}
