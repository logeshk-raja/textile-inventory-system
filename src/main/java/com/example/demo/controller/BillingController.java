package com.example.demo.controller;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Billing;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Order;
import com.example.demo.repository.BillingRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.OrderRepository;

@Controller
@RequestMapping("/billing")
public class BillingController {

    @Autowired
    private BillingRepository billingRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private OrderRepository orderRepo;

    /* =========================
       LIST BILLING DETAILS
       ========================= */
    @GetMapping("")
    public String billingList(Model model) {
        model.addAttribute("bills", billingRepo.findAll());
        model.addAttribute("content", "billing_list");
        return "layout";
    }

    /* =========================
       SHOW ADD BILL PAGE
       ========================= */
    @GetMapping("/add")
    public String showAddBill(Model model) {

        Billing billing = new Billing();

        // 🔥 IMPORTANT FIX (avoid null pointer in Thymeleaf)
        billing.setCustomer(new Customer());
        billing.setOrder(new Order());

        model.addAttribute("billing", billing);
        model.addAttribute("customers", customerRepo.findAll());
        model.addAttribute("orders", orderRepo.findAll());

        model.addAttribute("content", "billing_add");

        return "layout"; // layout will load billing_add
    }

    /* =========================
       SHOW EDIT BILL PAGE
       ========================= */
    @GetMapping("/edit/{id}")
    public String showEditBill(@PathVariable Long id, Model model) {

        Billing billing = billingRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid bill Id:" + id));

        // 🔥 SAFE NULL HANDLING
        if (billing.getCustomer() == null) {
            billing.setCustomer(new Customer());
        }

        if (billing.getOrder() == null) {
            billing.setOrder(new Order());
        }

        model.addAttribute("billing", billing);
        model.addAttribute("customers", customerRepo.findAll());
        model.addAttribute("orders", orderRepo.findAll());

        model.addAttribute("content", "billing_add");

        return "layout";
    }

    /* =========================
       SAVE / UPDATE BILL DETAILS
       ========================= */
    @PostMapping("/save")
    public String saveBill(@ModelAttribute Billing billing) {

        try {

            // 🔥 CUSTOMER FETCH FIX
            if (billing.getCustomer() != null && billing.getCustomer().getId() != null) {
                billing.setCustomer(
                        customerRepo.findById(billing.getCustomer().getId()).orElse(null)
                );
            } else {
                billing.setCustomer(null);
            }

            // 🔥 ORDER FETCH FIX
            if (billing.getOrder() != null && billing.getOrder().getId() != null) {
                billing.setOrder(
                        orderRepo.findById(billing.getOrder().getId()).orElse(null)
                );
            } else {
                billing.setOrder(null);
            }

            // 🔥 NEW BILL
            if (billing.getId() == null) {

                billing.setBillDate(LocalDate.now());

                billing.setBillNumber("BILL-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 6)
                        .toUpperCase());
            }

            // 🔥 DEFAULT STATUS
            if (billing.getPaymentStatus() == null || billing.getPaymentStatus().isEmpty()) {
                billing.setPaymentStatus("Pending");
            }

            billingRepo.save(billing);

        } catch (Exception e) {
            e.printStackTrace(); // check console for exact error
        }

        return "redirect:/billing";
    }

    /* =========================
       DELETE BILL
       ========================= */
    @GetMapping("/delete/{id}")
    public String deleteBill(@PathVariable Long id) {

        billingRepo.deleteById(id);

        return "redirect:/billing";
    }
}