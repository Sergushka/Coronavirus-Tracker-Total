package com.romsashka.trackcoronavirus.sources;

import com.romsashka.trackcoronavirus.models.LocationData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class LocationDataSource {
    private Map<String, LocationData> locationData;
}
