package br.com.henry.selective.uol.customer.rest.client.climate;

import br.com.henry.selective.uol.customer.model.entity.ClimateData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MetaWeatherClimateServiceTest.MetaWeatherClimateServiceTestConfig.class)
public class MetaWeatherClimateServiceTest {

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MetaWeatherClimateService metaWeatherClimateService;

    @Before
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testGetClimateForLocation() throws Exception {

        BigDecimal latitude = BigDecimal.valueOf(16.7);
        BigDecimal longitude = BigDecimal.valueOf(36.1);

        MetaWeatherClimateService.LocationResponse.LocationResponseData locationResponseData = new MetaWeatherClimateService.LocationResponse.LocationResponseData();
        locationResponseData.setDistance(1000L);
        locationResponseData.setLatt_long(latitude.toPlainString() + "," + longitude.toPlainString());
        locationResponseData.setLocation_type("City");
        locationResponseData.setTitle("Test City");
        locationResponseData.setWoeid(542L);
        MetaWeatherClimateService.LocationResponse locationResponse = new MetaWeatherClimateService.LocationResponse();
        locationResponse.add(locationResponseData);

        Map<String, String> locationParams = new HashMap<>(2);
        locationParams.put("lat", latitude.toPlainString());
        locationParams.put("lng", longitude.toPlainString());
        mockServer.expect(ExpectedCount.once(),
            requestTo(new UriTemplate(MetaWeatherClimateService.LOCATION_URI_TEMPLATE).expand(locationParams)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(mapper.writeValueAsString(locationResponse))
            );

        MetaWeatherClimateService.ClimateResponse.ConsolidatedWeather consolidatedWeather = new MetaWeatherClimateService.ClimateResponse.ConsolidatedWeather();
        consolidatedWeather.setMax_temp(BigDecimal.valueOf(54));
        consolidatedWeather.setMin_temp(BigDecimal.valueOf(14.3));
        MetaWeatherClimateService.ClimateResponse climateResponse = new MetaWeatherClimateService.ClimateResponse();
        climateResponse.setConsolidated_weather(Collections.singletonList(consolidatedWeather));

        mockServer.expect(ExpectedCount.once(),
            requestTo(new UriTemplate(MetaWeatherClimateService.CLIMATE_URI_TEMPLATE).expand(locationResponseData.getWoeid())))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(mapper.writeValueAsString(climateResponse))
            );

        ClimateData climateData = metaWeatherClimateService.getClimateForLocation(latitude, longitude);

        Assert.assertEquals(climateData.getMaximumTemperature(), consolidatedWeather.getMax_temp());
        Assert.assertEquals(climateData.getMinimumTemperature(), consolidatedWeather.getMin_temp());

        mockServer.verify();

    }

    @Configuration
    static class MetaWeatherClimateServiceTestConfig {
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }

        @Bean
        public MetaWeatherClimateService metaWeatherClimateService() {
            return new MetaWeatherClimateService();
        }
    }

}