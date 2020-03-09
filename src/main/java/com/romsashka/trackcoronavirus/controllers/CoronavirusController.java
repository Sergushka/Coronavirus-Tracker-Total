package com.romsashka.trackcoronavirus.controllers;

import com.romsashka.trackcoronavirus.models.LocationData;
import com.romsashka.trackcoronavirus.services.CoronavirusDataService;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/corona")
public class CoronavirusController {
    private final CoronavirusDataService coronavirusDataService;

    public CoronavirusController(CoronavirusDataService coronavirusDataService) {
        this.coronavirusDataService = coronavirusDataService;
    }

    @GetMapping(value = "/{country}")
    public List<LocationData> getCoronaByCountry(@PathVariable("country") String country) throws NotFound {
        return coronavirusDataService.getLocationDataSortedByCountry(country);
    }
}
