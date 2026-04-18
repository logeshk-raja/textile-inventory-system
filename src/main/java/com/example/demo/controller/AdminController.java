package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Order;

import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.SupplierRepository;

import java.util.List;
import java.io.IOException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SupplierRepository supplierRepository;


    // ================= ADMIN LOGIN PAGE =================
    @GetMapping("/login")
    public String loginPage() {
        return "admin_login";
    }


    // ================= LOGIN PROCESS =================
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        if ("admin".equals(username) && "admin123".equals(password)) {

            session.setAttribute("admin", true);

            return "redirect:/admin/dashboard";
        }

        model.addAttribute("error", "Invalid Username or Password");

        return "admin_login";
    }


    // ================= DASHBOARD =================
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("totalProducts", productRepository.count());
        model.addAttribute("totalOrders", orderRepository.count());
        model.addAttribute("totalCustomers", customerRepository.count());
        model.addAttribute("totalSuppliers", supplierRepository.count());

        model.addAttribute("content", "dashboard");

        return "layout";
    }


    // ================= VIEW ALL ORDERS =================
    @GetMapping("/orders")
    public String orders(HttpSession session, Model model) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("orders", orderRepository.findAll());
        model.addAttribute("content", "admin/orders");

        return "layout";
    }


    // ================= DOWNLOAD EXCEL =================
    @GetMapping("/orders/download")
    public void downloadOrders(HttpServletResponse response) throws IOException {

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=orders.xlsx");

        List<Order> orders = orderRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Orders");

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("User");
        header.createCell(2).setCellValue("Product");
        header.createCell(3).setCellValue("Qty");
        header.createCell(4).setCellValue("Total");
        header.createCell(5).setCellValue("Payment");
        header.createCell(6).setCellValue("Date");
        header.createCell(7).setCellValue("Status");

        int rowCount = 1;

        for (Order o : orders) {

            Row row = sheet.createRow(rowCount++);

            row.createCell(0).setCellValue(o.getId());

            row.createCell(1).setCellValue(
                o.getUser() != null ? o.getUser().getName() : "Unknown"
            );

            row.createCell(2).setCellValue(
                o.getProduct() != null ? o.getProduct().getProductName() : "N/A"
            );

            row.createCell(3).setCellValue(o.getQuantity());
            row.createCell(4).setCellValue(o.getTotalPrice());
            row.createCell(5).setCellValue(o.getPaymentMethod());

            row.createCell(6).setCellValue(
                o.getOrderDate() != null ? o.getOrderDate().toString() : ""
            );

            row.createCell(7).setCellValue(o.getStatus());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }


    // ================= VIEW ORDER DETAILS =================
    @GetMapping("/order/view/{id}")
    public String viewOrder(@PathVariable Long id,
                            HttpSession session,
                            Model model) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        Order order = orderRepository.findById(id).orElse(null);

        model.addAttribute("order", order);
        model.addAttribute("content", "admin/order_details");

        return "layout";
    }


    // ================= UPDATE ORDER STATUS =================
    @GetMapping("/order/status/{id}/{status}")
    public String updateOrderStatus(@PathVariable Long id,
                                    @PathVariable String status,
                                    HttpSession session) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        Order order = orderRepository.findById(id).orElse(null);

        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
        }

        return "redirect:/admin/orders";
    }


    // ================= REPORT PAGE =================
    @GetMapping("/reports")
    public String reportsPage(HttpSession session, Model model) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("content", "reports");

        return "layout";
    }


    // ================= LOGOUT =================
    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/admin/login";
    }
}