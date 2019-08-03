package br.com.henry.selective.uol.customer.rest.client.climate;

import br.com.henry.selective.uol.customer.model.entity.ClimateData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Slf4j
public class MetaWeatherClimateService implements ClimateService {

    static final String LOCATION_URI_TEMPLATE = "https://www.metaweather.com/api/location/search/?lattlong={lat},{lng}";
    static final String CLIMATE_URI_TEMPLATE = "https://www.metaweather.com/apilocation/{woeid}/";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public ClimateData getClimateForLocation(BigDecimal latitude, BigDecimal longitude) {
        log.info("Requesting location on MetaWeather for lat={} lng={}", latitude, longitude);
        Map<String, String> params = new HashMap<>(2);
        params.put("lat", latitude.toPlainString());
        params.put("lng", longitude.toPlainString());
        LocationResponse locationResponse = restTemplate.getForObject(LOCATION_URI_TEMPLATE, LocationResponse.class, params);
        if (locationResponse == null || locationResponse.isEmpty()) {
            return null;
        }
        return locationResponse.stream()
            .filter(loc -> "City".equalsIgnoreCase(loc.location_type))
            .min(Comparator.comparing(LocationResponse.LocationResponseData::getDistance))
            .map(this::retrieveClimateData)
            .orElse(null);
    }

    private ClimateData retrieveClimateData(LocationResponse.LocationResponseData locationResponseData) {
        return getClimateDataByWoeId(locationResponseData.woeid);
    }

    public ClimateData getClimateDataByWoeId(Long woeid) {
        ClimateResponse climateResponse = restTemplate.getForObject(CLIMATE_URI_TEMPLATE, ClimateResponse.class, Collections.singletonMap("woeid", woeid));
        if (climateResponse == null || climateResponse.consolidated_weather == null || climateResponse.consolidated_weather.isEmpty()) {
            return null;
        }
        return climateResponse.consolidated_weather.stream()
            .reduce((a, b) -> {
                a.max_temp = a.max_temp.add(b.max_temp).divide(BigDecimal.valueOf(2), RoundingMode.HALF_EVEN);
                a.min_temp = a.min_temp.add(b.min_temp).divide(BigDecimal.valueOf(2), RoundingMode.HALF_EVEN);
                return a;
            })
            .map(this::mapClimateData)
            .orElse(null);
    }

    private ClimateData mapClimateData(ClimateResponse.ConsolidatedWeather consolidatedWeather) {
        ClimateData climateData = new ClimateData();
        climateData.setMaximumTemperature(consolidatedWeather.max_temp);
        climateData.setMinimumTemperature(consolidatedWeather.min_temp);
        return climateData;
    }

    static class LocationResponse extends ArrayList<LocationResponse.LocationResponseData> {

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @SuppressWarnings("squid:S00116")
        static class LocationResponseData {
            private Long distance;
            private String title;
            private String location_type;
            private Long woeid;
            private String latt_long;
        }

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @SuppressWarnings("squid:S00116")
    static class ClimateResponse {

        private List<ConsolidatedWeather> consolidated_weather;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @SuppressWarnings("squid:S00116")
        static class ConsolidatedWeather {
            private BigDecimal min_temp;
            private BigDecimal max_temp;
        }

    }

}
