package br.com.henry.selective.uol.customer.test;

import br.com.henry.selective.uol.customer.model.entity.ClimateData;
import br.com.henry.selective.uol.customer.model.entity.Customer;
import br.com.henry.selective.uol.customer.model.entity.LocationData;

import static org.mockito.Mockito.mock;

public final class TestUtils {

    private TestUtils() {
        // Static class
    }

    public static Customer getStandardTestCustomer() {
        Long id = 5L;
        Integer age = 15;
        String name = "Test Name";
        return getTestCustomer(id, age, name);
    }

    public static Customer getTestCustomer(Long id, Integer age, String name) {
        ClimateData climateData = mock(ClimateData.class);
        LocationData locationData = mock(LocationData.class);
        Customer customer = new Customer();
        customer.setName(name);
        customer.setAge(age);
        customer.setId(id);
        customer.setClimate(climateData);
        customer.setLocation(locationData);
        return customer;
    }

}
