package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepository;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    // LIST
    @GetMapping
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("pageTitle", "Customers");
        model.addAttribute("content", "customer_list");
        return "layout";
    }

    // ADD PAGE
    @GetMapping("/add")
    public String addCustomer(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("pageTitle", "Add Customer");
        model.addAttribute("content", "add_customer");
        return "layout";
    }

    @PostMapping("/save")
    public String saveCustomer(@ModelAttribute Customer customer) {

        // Safety default only if user doesn't choose
        if (customer.getStatus() == null) {
            customer.setStatus("Active");
        }

        customerRepository.save(customer);
        return "redirect:/customers";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id) {

        try {
            customerRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Delete Error: " + e.getMessage());
        }

        return "redirect:/customers";
    }
}
