package com.codexaa.service;

import com.codexaa.model.Customer;

import java.util.List;

public interface CustomerService {
    Customer createCustomer(Customer customer);
    Customer updateCustomer(Long id,Customer customer);
    void deleteCustomer(Long id);
    Customer getCustomer(Long id);
    List<Customer> getAllCustomer();
    List<Customer> searchCustomer(String keyword);


}
