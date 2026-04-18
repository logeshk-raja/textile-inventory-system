package com.example.demo.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Order;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;

import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // ================= LIST ORDERS (ADMIN) =================
    @GetMapping
    public String listOrders(Model model) {

        model.addAttribute("orders", orderRepository.findAll());

        // 🔥 THESE TWO LINES ARE MUST
        model.addAttribute("pageTitle", "Orders");
        model.addAttribute("content", "admin/admin_orders");

        return "layout";   // 🔥 IMPORTANT
    }

    // ================= ADD ORDER PAGE =================
    @GetMapping("/add")
    public String addOrder(Model model) {
        model.addAttribute("order", new Order());
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("customers", customerRepository.findAll());
        return "admin/add_order";      // (layout vendam)
    }
    
    @GetMapping("/download")
    public void downloadOrders(HttpServletResponse response) throws Exception {

        response.setContentType("text/csv");
        response.setHeader(
            "Content-Disposition",
            "attachment; filename=orders.csv"
        );

        List<Order> orders = orderRepository.findAll();

        PrintWriter writer = response.getWriter();

        // CSV HEADER
        writer.println("Order ID,User,Product,Quantity,Total,Status");

        // CSV DATA
        for (Order o : orders) {
            writer.println(
                o.getId() + "," +
                (o.getUser() != null ? o.getUser().getName() : "N/A") + "," +
                o.getProduct().getProductName() + "," +
                o.getQuantity() + "," +
                o.getTotalPrice() + "," +
                o.getStatus()
            );
        }

        writer.flush();
        writer.close();
    }

    // ================= SAVE ORDER =================
    @PostMapping("/save")
    public String saveOrder(@ModelAttribute Order order) {

        order.setOrderDate(LocalDate.now());

        // price always from product
        double price = order.getProduct().getPrice();
        int qty = order.getQuantity();

        order.setTotalPrice(price * qty);
        order.setStatus("Pending");

        orderRepository.save(order);

        return "redirect:/orders";
    }

    // ================= DELETE ORDER =================
    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderRepository.deleteById(id);
        return "redirect:/orders";
    }
}