package br.com.henry.selective.uol.customer.rest.controller;

import br.com.henry.selective.uol.customer.model.dto.CustomerCreation;
import br.com.henry.selective.uol.customer.model.entity.Customer;
import br.com.henry.selective.uol.customer.repository.CustomerRepository;
import br.com.henry.selective.uol.customer.test.SSLUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.StringUtils;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerRepository customerRepository;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeClass
    public static void beforeClass() throws Exception {
        SSLUtil.turnOffSslChecking();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        SSLUtil.turnOnSslChecking();
    }

    @Test
    public void testCustomerCreation() throws Exception {

        when(customerRepository.save(any())).then(c -> {
            Customer customer = c.getArgument(0);
            customer.setId(1L);
            Assert.assertNotNull(customer.getLocation());
            Assert.assertNotNull(StringUtils.trimAllWhitespace(customer.getLocation().getCityName()));
            Assert.assertNotNull(StringUtils.trimAllWhitespace(customer.getLocation().getContinentName()));
            Assert.assertNotNull(StringUtils.trimAllWhitespace(customer.getLocation().getCountryName()));
            Assert.assertNotNull(customer.getLocation().getLongitude());
            Assert.assertNotNull(customer.getLocation().getLatitude());
            Assert.assertNotNull(customer.getClimate());
            Assert.assertNotNull(customer.getClimate().getMinimumTemperature());
            Assert.assertNotNull(customer.getClimate().getMaximumTemperature());
            return customer;
        });

        CustomerCreation customerCreation = new CustomerCreation();
        customerCreation.setName("Test Name");
        customerCreation.setAge(15);

        this.mockMvc.perform(post("/customer")
            .with(remoteAddr("8.8.8.8"))
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

        verify(customerRepository, times(1)).save(customerArgumentCaptor.capture());

        Customer savedCustomer = customerArgumentCaptor.getValue();

        Assert.assertEquals(savedCustomer.getAge(), customerCreation.getAge());
        Assert.assertEquals(savedCustomer.getName(), customerCreation.getName());
    }

    private static RequestPostProcessor remoteAddr(final String remoteAddr) {
        return new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setRemoteAddr(remoteAddr);
                return request;
            }
        };
    }

}