package br.com.henry.selective.uol.customer.rest.client.geolocation;

import br.com.henry.selective.uol.customer.model.entity.LocationData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;

@Service
@Slf4j
public class IpVigilanteGeoLocalizationService implements GeoLocalizationService {

    private static final String URI_TEMPLATE = "https://ipvigilante.com/json/{ip}/default";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public LocationData getLocationForIp(String ip) {
        log.info("Requesting location on IpVigilante for ip {}", ip);
        LocationResponse locationResponse = restTemplate.getForObject(URI_TEMPLATE, LocationResponse.class, Collections.singletonMap("ip", ip));
        if (locationResponse == null || locationResponse.data == null) {
            return null;
        }
        return convertLocationData(locationResponse.data);
    }

    private LocationData convertLocationData(LocationResponse.LocationResponseData locationResponseData) {
        LocationData locationData = new LocationData();
        locationData.setCityName(locationResponseData.city_name);
        locationData.setContinentName(locationResponseData.continent_name);
        locationData.setCountryName(locationResponseData.country_name);
        locationData.setLatitude(new BigDecimal(locationResponseData.latitude));
        locationData.setLongitude(new BigDecimal(locationResponseData.longitude));
        return locationData;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class LocationResponse {
        private String status;
        private LocationResponseData data;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @SuppressWarnings("squid:S00116")
        private static class LocationResponseData {
            private String continent_name;
            private String country_name;
            private String city_name;
            private String latitude;
            private String longitude;
        }

    }
}
