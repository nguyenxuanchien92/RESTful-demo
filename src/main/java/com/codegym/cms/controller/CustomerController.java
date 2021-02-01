package com.codegym.cms.controller;

import java.util.List;

import com.codegym.cms.model.Customer;
import com.codegym.cms.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    //region Retrieve all customers

    @RequestMapping(value = "/customers", method = RequestMethod.GET)
    public ResponseEntity<List<Customer>> listAllCustomers() {

        List<Customer> customers = customerService.findAll();
        if (customers.isEmpty()) {
            return new ResponseEntity<List<Customer>>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<Customer>>(customers, HttpStatus.OK);
    }

    //endregion

    //region Single customer

    @RequestMapping(value = "/customers/{id}"
            , method = RequestMethod.GET
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> getCustomer(@PathVariable("id") Long id) {
        System.out.println("Fetching Customer with id: " + id);
        Customer customer = customerService.finById(id);
        if (customer == null) {
            System.out.println("Customer with id " + id + "not found");
            return new ResponseEntity<Customer>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Customer>(customer, HttpStatus.OK);
    }

    //endregion

    //region Update customer

    @RequestMapping(value = "/customers/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Customer> updateCustomer(@PathVariable("id") long id,
                                                   @RequestBody Customer customer) {
        System.out.println("Updating Customer:" + id);
        Customer currentCustomer = customerService.finById(id);
        if (currentCustomer == null) {
            System.out.println("Customer with id:" + id + "not found");
            return new ResponseEntity<Customer>(HttpStatus.NOT_FOUND);
        }

        currentCustomer.setFirstName(customer.getFirstName());
        currentCustomer.setLastName(customer.getLastName());
        currentCustomer.setId(customer.getId());

        customerService.save(currentCustomer);

        return new ResponseEntity<Customer>(currentCustomer, HttpStatus.OK);
    }

    //endregion

    //region Delete customer

    @RequestMapping(value = "/customers/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Customer> deleteCustomer(@PathVariable("id") long id) {
        System.out.println("Fetching & Deleting Customer with id:" + id);
        Customer customer = customerService.finById(id);
        if (customer == null) {
            System.out.println("Unable to delete. Customer with id:" + id + "not found");
            return new ResponseEntity<Customer>(HttpStatus.NOT_FOUND);
        }

        customerService.delete(id);

        return new ResponseEntity<Customer>(HttpStatus.NO_CONTENT);
    }

    //endregion

    //region Create customer
    @RequestMapping(value = "/customers/",
            method = RequestMethod.POST,
            consumes =  MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createCustomer(@RequestBody Customer customer, UriComponentsBuilder ucBuilder) {
        System.out.println("Create customer: " + customer.getLastName());
        customerService.save(customer);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/customers/{id}").buildAndExpand(customer.getId()).toUri());

        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }
}
