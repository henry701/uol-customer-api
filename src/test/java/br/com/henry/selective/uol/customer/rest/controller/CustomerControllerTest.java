package br.com.henry.selective.uol.customer.rest.controller;

import br.com.henry.selective.uol.customer.model.dto.CustomerCreation;
import br.com.henry.selective.uol.customer.model.dto.CustomerUpdate;
import br.com.henry.selective.uol.customer.model.entity.Customer;
import br.com.henry.selective.uol.customer.model.entity.LocationData;
import br.com.henry.selective.uol.customer.repository.CustomerRepository;
import br.com.henry.selective.uol.customer.rest.client.climate.ClimateService;
import br.com.henry.selective.uol.customer.rest.client.geolocation.GeoLocalizationService;
import br.com.henry.selective.uol.customer.test.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GeoLocalizationService geoLocalizationService;

    @MockBean
    private ClimateService climateService;

    @MockBean
    private CustomerRepository customerRepository;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCustomerCreation() throws Exception {

        when(customerRepository.save(any())).then(c -> {
            Customer customer = (Customer) c.getArgument(0);
            customer.setId(1L);
            return customer;
        });

        BigDecimal lat = BigDecimal.valueOf(15.4d);
        BigDecimal lng = BigDecimal.valueOf(30.5d);

        LocationData locationData = mock(LocationData.class);
        when(geoLocalizationService.getLocationForIp(any())).thenReturn(locationData);
        when(locationData.getLatitude()).thenReturn(lat);
        when(locationData.getLongitude()).thenReturn(lng);

        CustomerCreation customerCreation = new CustomerCreation();
        customerCreation.setName("Test Name");
        customerCreation.setAge(15);

        this.mockMvc.perform(post("/customer")
            .content(mapper.writeValueAsString(customerCreation))
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.climate").doesNotExist())
            .andExpect(jsonPath("$.location").doesNotExist())
            .andExpect(jsonPath("$.age").value(customerCreation.getAge()))
            .andExpect(jsonPath("$.name").value(customerCreation.getName()))
            .andExpect(jsonPath("$.id").value(1L));

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(climateService, times(1)).getClimateForLocation(lat, lng);
        verify(customerRepository, times(1)).save(customerArgumentCaptor.capture());

        Customer savedCustomer = customerArgumentCaptor.getValue();

        Assert.assertEquals(savedCustomer.getAge(), customerCreation.getAge());
        Assert.assertEquals(savedCustomer.getName(), customerCreation.getName());
    }

    @Test
    public void testWrongCustomerCreation() throws Exception {

        CustomerCreation customerCreation = new CustomerCreation();
        customerCreation.setName("");
        customerCreation.setAge(-50);

        this.mockMvc.perform(post("/customer")
            .content(mapper.writeValueAsString(customerCreation))
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.climate").doesNotExist())
            .andExpect(jsonPath("$.location").doesNotExist())
            .andExpect(jsonPath("$.age").exists())
            .andExpect(jsonPath("$.name").exists())
            .andExpect(jsonPath("$.id").doesNotExist());

        verify(climateService, never()).getClimateForLocation(any(), any());
        verify(customerRepository, never()).save(any());
    }

    @Test
    public void testCustomerQuery() throws Exception {

        Customer customer = TestUtils.getStandardTestCustomer();

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        this.mockMvc.perform(get("/customer/{id}", customer.getId())
            .accept(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.climate").doesNotExist())
            .andExpect(jsonPath("$.location").doesNotExist())
            .andExpect(jsonPath("$.age").value(customer.getAge()))
            .andExpect(jsonPath("$.name").value(customer.getName()))
            .andExpect(jsonPath("$.id").value(customer.getId()));

        verify(customerRepository, times(1)).findById(customer.getId());
        verify(customerRepository, never()).findAll();
    }

    @Test
    public void testNotFoundCustomerQuery() throws Exception {

        Long id = 99L;

        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        this.mockMvc.perform(get("/customer/{id}", id)
            .accept(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.climate").doesNotExist())
            .andExpect(jsonPath("$.location").doesNotExist())
            .andExpect(jsonPath("$.age").doesNotExist())
            .andExpect(jsonPath("$.name").doesNotExist())
            .andExpect(jsonPath("$.id").doesNotExist());

        verify(customerRepository, times(1)).findById(id);
        verify(customerRepository, never()).findAll();
    }

    @Test
    public void testCustomerList() throws Exception {

        Customer customer1 = TestUtils.getStandardTestCustomer();
        Customer customer2 = TestUtils.getStandardTestCustomer();

        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer1, customer2));

        this.mockMvc.perform(get("/customer")
            .accept(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$[0].climate").doesNotExist())
            .andExpect(jsonPath("$[0].location").doesNotExist())
            .andExpect(jsonPath("$[1].climate").doesNotExist())
            .andExpect(jsonPath("$[1].location").doesNotExist())
            .andExpect(jsonPath("$[0].age").value(customer1.getAge()))
            .andExpect(jsonPath("$[0].name").value(customer1.getName()))
            .andExpect(jsonPath("$[0].id").value(customer1.getId()))
            .andExpect(jsonPath("$[1].age").value(customer2.getAge()))
            .andExpect(jsonPath("$[1].name").value(customer2.getName()))
            .andExpect(jsonPath("$[1].id").value(customer2.getId()));

        verify(customerRepository, times(1)).findAll();
        verify(customerRepository, never()).findById(any());
    }

    @Test
    public void testCustomerPatch() throws Exception {

        Customer customer = TestUtils.getStandardTestCustomer();

        CustomerUpdate customerUpdate = new CustomerUpdate();
        customerUpdate.setName("Updated Name");
        customerUpdate.setAge(25);
        customerUpdate.setId(customer.getId());

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any())).then(c -> c.getArgument(0));

        this.mockMvc.perform(patch("/customer")
            .content(mapper.writeValueAsString(customerUpdate))
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.climate").doesNotExist())
            .andExpect(jsonPath("$.location").doesNotExist())
            .andExpect(jsonPath("$.age").value(customerUpdate.getAge()))
            .andExpect(jsonPath("$.name").value(customerUpdate.getName()))
            .andExpect(jsonPath("$.id").value(customer.getId()));

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerRepository, times(1)).findById(customer.getId());
        verify(customerRepository, times(1)).save(customerArgumentCaptor.capture());
        verify(customerRepository, never()).findAll();

        Customer savedCustomer = customerArgumentCaptor.getValue();
        Assert.assertEquals(savedCustomer.getName(), customerUpdate.getName());
        Assert.assertEquals(savedCustomer.getAge(), customerUpdate.getAge());
    }

    @Test
    public void testCustomerDelete() throws Exception {

        Customer customer = TestUtils.getStandardTestCustomer();

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        this.mockMvc.perform(delete("/customer/{id}", customer.getId())
            .accept(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.climate").doesNotExist())
            .andExpect(jsonPath("$.location").doesNotExist())
            .andExpect(jsonPath("$.age").value(customer.getAge()))
            .andExpect(jsonPath("$.name").value(customer.getName()))
            .andExpect(jsonPath("$.id").value(customer.getId()));

        verify(customerRepository, times(1)).findById(customer.getId());
        verify(customerRepository, times(1)).deleteById(customer.getId());
        verify(customerRepository, never()).findAll();
    }

}