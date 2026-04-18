package com.example.demo.controller;

import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.entity.*;
import com.example.demo.repository.*;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BillingRepository billingRepository;

    // ================= PRODUCT REPORT =================
    @GetMapping("/products")
    public void productReport(HttpServletResponse response) throws Exception {

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=product_report.csv");

        List<Product> products = productRepository.findAll();

        PrintWriter writer = response.getWriter();

        writer.println("ID,Product Name,Quantity,Price");

        for (Product p : products) {
            writer.println(
                    p.getId() + "," +
                    p.getProductName() + "," +
                    p.getQuantity() + "," +
                    p.getPrice()
            );
        }

        writer.flush();
        writer.close();
    }

    // ================= CUSTOMER REPORT =================
    @GetMapping("/customers")
    public void customerReport(HttpServletResponse response) throws Exception {

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=customer_report.csv");

        List<Customer> customers = customerRepository.findAll();

        PrintWriter writer = response.getWriter();

        writer.println("ID,Name,Phone,Email,Address");

        for (Customer c : customers) {
            writer.println(
                    c.getId() + "," +
                    c.getName() + "," +
                    c.getPhone() + "," +
                    c.getEmail() + "," +
                    c.getAddress()
            );
        }

        writer.flush();
        writer.close();
    }

    // ================= SUPPLIER REPORT =================
    @GetMapping("/suppliers")
    public void supplierReport(HttpServletResponse response) throws Exception {

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=supplier_report.csv");

        List<Supplier> suppliers = supplierRepository.findAll();

        PrintWriter writer = response.getWriter();

        writer.println("ID,Name,Phone,Email,Address");

        for (Supplier s : suppliers) {
            writer.println(
                    s.getId() + "," +
                    s.getName() + "," +
                    s.getPhone() + "," +
                    s.getEmail() + "," +
                    s.getAddress()
            );
        }

        writer.flush();
        writer.close();
    }

    // ================= ORDER REPORT (FIXED 🔥) =================
    @GetMapping("/orders")
    public void orderReport(HttpServletResponse response) throws Exception {

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=order_report.csv");

        List<Order> orders = orderRepository.findAll();

        PrintWriter writer = response.getWriter();

        writer.println("OrderID,User,Product,Quantity,TotalPrice,Payment,Date,Status");

        for (Order o : orders) {

            String user = (o.getUser() != null) ? o.getUser().getName() : "Unknown";
            String product = (o.getProduct() != null) ? o.getProduct().getProductName() : "N/A";

            writer.println(
                o.getId() + "," +
                user + "," +
                product + "," +
                o.getQuantity() + "," +
                o.getTotalPrice() + "," +
                o.getPaymentMethod() + "," +
                (o.getOrderDate() != null ? o.getOrderDate() : "") + "," +
                o.getStatus()
            );
        }

        writer.flush();
        writer.close();
    }

    // ================= BILLING REPORT =================
    @GetMapping("/billing")
    public void billingReport(HttpServletResponse response) throws Exception {

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=billing_report.csv");

        List<Billing> bills = billingRepository.findAll();

        PrintWriter writer = response.getWriter();

        writer.println("BillNo,Customer,Amount,Status");

        for (Billing b : bills) {
            writer.println(
                    b.getBillNumber() + "," +
                    b.getCustomer().getName() + "," +
                    b.getGrandTotal() + "," +
                    b.getPaymentStatus()
            );
        }

        writer.flush();
        writer.close();
    }
}