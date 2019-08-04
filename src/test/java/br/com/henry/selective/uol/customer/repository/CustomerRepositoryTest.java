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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @MockBean
    private CacheManager cacheManager;

    @MockBean
    private Cache cache;

    @Test
    public void testFullCustomerSave() throws Exception {

        when(cacheManager.getCache("customers")).thenReturn(cache);

        Customer customer = TestUtils.getStandardTestCustomer();
        customer.setId(null);
        putCustomerMetadata(customer);

        customer = customerRepository.save(customer);

        verify(cacheManager, atLeastOnce()).getCache("customers");
        verify(cache, times(1)).evict(customer.getId());

        Assert.assertNotNull(customerRepository.findById(customer.getId()));
    }

    @Test
    public void testCustomerDelete() throws Exception {

        when(cacheManager.getCache("customers")).thenReturn(mock(Cache.class));

        Customer customer = TestUtils.getStandardTestCustomer();
        putCustomerMetadata(customer);
        customer.setId(null);
        customer = customerRepository.save(customer);

        when(cacheManager.getCache("customers")).thenReturn(cache);

        customerRepository.deleteById(customer.getId());

        verify(cacheManager, atLeastOnce()).getCache("customers");
        verify(cache, times(1)).evict(customer.getId());
    }

    @Test
    public void testCustomerUpdate() throws Exception {

        when(cacheManager.getCache("customers")).thenReturn(mock(Cache.class));

        Customer customer = TestUtils.getStandardTestCustomer();
        putCustomerMetadata(customer);
        customer.setId(null);
        customer = customerRepository.save(customer);

        when(cacheManager.getCache("customers")).thenReturn(cache);

        customer.setName("Changed Updated Test Name");
        customerRepository.save(customer);

        verify(cacheManager, atLeastOnce()).getCache("customers");
        verify(cache, times(1)).evict(customer.getId());
    }

    @Test
    public void testCustomerClear() throws Exception {
        when(cacheManager.getCache("customers")).thenReturn(cache);

        customerRepository.deleteAll();

        verify(cacheManager, atLeastOnce()).getCache("customers");
        verify(cache, times(1)).clear();
    }

    private void putCustomerMetadata(Customer customer) {

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
    }

}