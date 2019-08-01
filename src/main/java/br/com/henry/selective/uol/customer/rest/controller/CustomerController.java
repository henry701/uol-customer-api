package br.com.henry.selective.uol.customer.rest.controller;

import br.com.henry.selective.uol.customer.model.Customer;
import br.com.henry.selective.uol.customer.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping(value = "/customer", consumes = MediaType.APPLICATION_JSON_VALUE, name = "create-customer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> create(@Valid @RequestBody Customer customer) {
        customer.setId(null);
        // TODO: Aggregate infos from the two other APIs
        customer = customerRepository.save(customer);
        return ResponseEntity.ok().body(customer);
    }

    @GetMapping(value = "/customer/{id}", name = "get-customer-by-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> getById(@PathVariable(name = "id") Long id) {
        return customerRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping(value = "/customer", name = "update-customer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> update(@Valid @RequestBody Customer newCustomer) {
        return customerRepository.findById(newCustomer.getId())
            .map(baseCustomer -> {
                // TODO: Patch fields using reflection
                return ResponseEntity.ok(customerRepository.save(baseCustomer));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/customer/{id}", name = "delete-customer-by-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> delete(@PathVariable(name = "id") Long id) {
        return customerRepository.findById(id)
            .map(customer -> {
                customerRepository.deleteById(id);
                return ResponseEntity.ok(customer);
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/customer", name = "get-customer-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<Customer>> list() {
        return ResponseEntity.ok(customerRepository.findAll());
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
}
