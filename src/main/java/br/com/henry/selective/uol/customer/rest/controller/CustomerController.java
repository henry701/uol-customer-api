package br.com.henry.selective.uol.customer.rest.controller;

import br.com.henry.selective.uol.customer.model.dto.CustomerCreation;
import br.com.henry.selective.uol.customer.model.dto.CustomerResponse;
import br.com.henry.selective.uol.customer.model.dto.CustomerUpdate;
import br.com.henry.selective.uol.customer.model.entity.ClimateData;
import br.com.henry.selective.uol.customer.model.entity.Customer;
import br.com.henry.selective.uol.customer.model.entity.LocationData;
import br.com.henry.selective.uol.customer.repository.CustomerRepository;
import br.com.henry.selective.uol.customer.rest.client.climate.ClimateService;
import br.com.henry.selective.uol.customer.rest.client.geolocation.GeoLocalizationService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RestController
@Slf4j
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GeoLocalizationService geoLocalizationService;

    @Autowired
    private ClimateService climateService;

    @PostMapping(value = "/customer", consumes = MediaType.APPLICATION_JSON_VALUE, name = "create-customer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerCreation customerCreation, HttpServletRequest request) {
        Customer customer = modelMapper.map(customerCreation, Customer.class);
        attemptLocationAndClimateAggregation(customer, request.getRemoteAddr());
        customer = customerRepository.save(customer);
        CustomerResponse customerResponse = modelMapper.map(customer, CustomerResponse.class);
        return ResponseEntity.ok().body(customerResponse);
    }

    @GetMapping(value = "/customer/{id}", name = "get-customer-by-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerResponse> getById(@PathVariable(name = "id") Long id) {
        return customerRepository.findById(id)
            .map(customer -> ResponseEntity.ok(modelMapper.map(customer, CustomerResponse.class)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping(value = "/customer", name = "update-customer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerResponse> update(@Valid @RequestBody CustomerUpdate newCustomer) {
        return customerRepository.findById(newCustomer.getId())
            .map(baseCustomer -> {
                modelMapper.map(newCustomer, baseCustomer);
                baseCustomer = customerRepository.save(baseCustomer);
                return ResponseEntity.ok(modelMapper.map(baseCustomer, CustomerResponse.class));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/customer/{id}", name = "delete-customer-by-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerResponse> delete(@PathVariable(name = "id") Long id) {
        return customerRepository.findById(id)
            .map(customer -> {
                customerRepository.deleteById(id);
                return ResponseEntity.ok(modelMapper.map(customer, CustomerResponse.class));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/customer", name = "get-customer-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Stream<CustomerResponse>> list() {
        return ResponseEntity.ok(StreamSupport.stream(customerRepository.findAll().spliterator(), true)
            .map(c -> modelMapper.map(c, CustomerResponse.class)));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
        MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        }
        return errors;
    }

    private void attemptLocationAndClimateAggregation(Customer customer, String remoteIp) {
        attemptLocationAggregation(customer, remoteIp);
        attemptClimateAggregation(customer);
    }

    private void attemptLocationAggregation(Customer customer, String remoteIp) {
        try {
            LocationData location = geoLocalizationService.getLocationForIp(remoteIp);
            customer.setLocation(location);
        } catch (Exception e) {
            log.warn("Unable to get location for ip {}, registering customer without location and climate information!", remoteIp, e);
        }
    }

    private void attemptClimateAggregation(Customer customer) {
        LocationData locationData = customer.getLocation();
        if (locationData == null) {
            return;
        }
        try {
            ClimateData climate = climateService.getClimateForLocation(locationData.getLatitude(), locationData.getLongitude());
            customer.setClimate(climate);
        } catch (Exception e) {
            log.warn("Unable to get climate for lat={} lng={}, registering customer without climate information!", locationData.getLatitude(), locationData.getLongitude(), e);
        }
    }

}
