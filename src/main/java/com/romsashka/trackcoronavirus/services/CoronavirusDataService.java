package com.romsashka.trackcoronavirus.services;

import com.romsashka.trackcoronavirus.models.LocationData;
import com.romsashka.trackcoronavirus.sources.LocationDataSource;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import static org.springframework.util.StringUtils.capitalize;

@Service
public class CoronavirusDataService {
    private static final String CONFIRMED_VIRUS_CASES_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";
    private static final String ALL_CASE = "all";
    private LocationDataSource dataSource;

    @PostConstruct
    @Scheduled(cron = "0 * 1,13 * * *")
    public void getVirusConfirmedCases() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            Map<String, LocationData> newLocationData = new HashMap<>();
            HttpGet request = new HttpGet(CONFIRMED_VIRUS_CASES_URL);
            CloseableHttpResponse response = httpClient.execute(request);

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String result = EntityUtils.toString(entity);
                StringReader reader = new StringReader(result);

                Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
                for (CSVRecord record : records) {
                    long totalCases = Integer.parseInt(record.get(record.size() - 1));
                    long casesYesterday = Integer.parseInt(record.get(record.size() - 2));
                    String state = record.get("Province/State");
                    LocationData locationData = LocationData.builder()
                            .states(Collections.singletonList(state))
                            .country(record.get("Country/Region"))
                            .totalCases(totalCases)
                            .differenceWithYesterday(totalCases - casesYesterday)
                            .build();
                    LocationData data = newLocationData.get(locationData.getCountry());
                    if (data == null) {
                        newLocationData.put(locationData.getCountry(), locationData);
                    } else {
                        List<String> updatedStates = new ArrayList<>(data.getStates());
                        updatedStates.add(state);

                        LocationData updatedData = LocationData.builder()
                                .states(updatedStates)
                                .country(data.getCountry())
                                .totalCases(data.getTotalCases() + locationData.getTotalCases())
                                .differenceWithYesterday(data.getDifferenceWithYesterday() + locationData.getDifferenceWithYesterday())
                                .build();
                        newLocationData.put(locationData.getCountry(), updatedData);
                    }
                }
                dataSource = new LocationDataSource(newLocationData);
            }
        }
    }

    public List<LocationData> getLocationsDataSortedByTotalCases() {
        Map<String, LocationData> dataSourceLocationData = dataSource.getLocationData();
        List<LocationData> locationsData = new ArrayList<>(dataSourceLocationData.values());
        locationsData.sort(Comparator.comparingLong(LocationData::getTotalCases).reversed());
        return locationsData;
    }

    public List<LocationData> getLocationDataSortedByCountry(String country) throws NotFound {
        Map<String, LocationData> dataSourceLocationData = dataSource.getLocationData();
        country = capitalize(country);

        if (ALL_CASE.equalsIgnoreCase(country)) {
            return getLocationsDataSortedByTotalCases();
        }

        for (String key : dataSourceLocationData.keySet()) {
            if (key.toLowerCase().contains(country.toLowerCase())) {
                country = key;
            }
        }

        LocationData countryData = dataSourceLocationData.get(country);

        if (countryData != null) {
            return Collections.singletonList(countryData);
        } else {
            throw new NotFound();
        }
    }
}
