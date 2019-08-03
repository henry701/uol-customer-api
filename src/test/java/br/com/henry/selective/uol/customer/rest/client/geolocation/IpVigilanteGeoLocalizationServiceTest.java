package br.com.henry.selective.uol.customer.rest.client.geolocation;

import br.com.henry.selective.uol.customer.model.entity.LocationData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = IpVigilanteGeoLocalizationServiceTest.TestConfig.class)
public class IpVigilanteGeoLocalizationServiceTest {

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private IpVigilanteGeoLocalizationService ipVigilanteGeoLocalizationService;

    @Before
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testGetLocationForIp() throws Exception {

        String ipAddress = "127.0.0.1";

        BigDecimal latitude = BigDecimal.valueOf(16.7);
        BigDecimal longitude = BigDecimal.valueOf(36.1);

        IpVigilanteGeoLocalizationService.LocationResponse.LocationResponseData locationResponseData = new IpVigilanteGeoLocalizationService.LocationResponse.LocationResponseData();
        locationResponseData.setCity_name("Test City");
        locationResponseData.setContinent_name("Test Continent");
        locationResponseData.setCountry_name("Test Country");
        locationResponseData.setLatitude(latitude.toPlainString());
        locationResponseData.setLongitude(longitude.toPlainString());
        IpVigilanteGeoLocalizationService.LocationResponse locationResponse = new IpVigilanteGeoLocalizationService.LocationResponse();
        locationResponse.setData(locationResponseData);
        locationResponse.setStatus("success");

        mockServer.expect(ExpectedCount.once(),
            requestTo(new UriTemplate(IpVigilanteGeoLocalizationService.URI_TEMPLATE).expand(ipAddress)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(mapper.writeValueAsString(locationResponse))
            );

        LocationData locationData = ipVigilanteGeoLocalizationService.getLocationForIp(ipAddress);

        mockServer.verify();

        Assert.assertEquals(locationData.getLatitude(), new BigDecimal(locationResponseData.getLatitude()));
        Assert.assertEquals(locationData.getLongitude(), new BigDecimal(locationResponseData.getLongitude()));
        Assert.assertEquals(locationData.getCityName(), locationResponseData.getCity_name());
        Assert.assertEquals(locationData.getCountryName(), locationResponseData.getCountry_name());
        Assert.assertEquals(locationData.getContinentName(), locationResponseData.getContinent_name());

    }

    static class TestConfig {
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }

        @Bean
        public IpVigilanteGeoLocalizationService ipVigilanteGeoLocalizationService() {
            return new IpVigilanteGeoLocalizationService();
        }
    }

}