package br.com.henry.selective.uol.customer.repository;

import br.com.henry.selective.uol.customer.model.entity.ClimateData;
import br.com.henry.selective.uol.customer.model.entity.Customer;
import br.com.henry.selective.uol.customer.model.entity.LocationData;
import br.com.henry.selective.uol.customer.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void testFullCustomer() throws Exception {
        Customer customer = TestUtils.getStandardTestCustomer();

        LocationData locationData = new LocationData();
        customer.setLocation(locationData);

        locationData.setLongitude(BigDecimal.valueOf(20));
        locationData.setLatitude(BigDecimal.valueOf(50));
        locationData.setCountryName("Test Country");
        locationData.setCityName("Test City");
        locationData.setContinentName("Test Continent");

        ClimateData climateData = new ClimateData();
        customer.setClimate(climateData);

        climateData.setMaximumTemperature(BigDecimal.valueOf(67.2));
        climateData.setMinimumTemperature(BigDecimal.valueOf(37.2));

        customer = customerRepository.save(customer);

        Assert.assertNotNull(customerRepository.findById(customer.getId()));
    }

}