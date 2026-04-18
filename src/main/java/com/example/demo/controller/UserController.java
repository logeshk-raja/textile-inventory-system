package com.example.demo.controller;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.User;
import com.example.demo.entity.Product;
import com.example.demo.entity.Order;
import com.example.demo.entity.Billing;

import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.BillingRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private BillingRepository billingRepo;


    // ================= LOGIN PAGE =================
    @GetMapping("/login")
    public String loginPage() {
        return "user/user_login";
    }

    // ================= REGISTER PAGE =================
    @GetMapping("/register")
    public String registerPage() {
        return "user/user_register";
    }

    // ================= REGISTER USER =================
    @PostMapping("/register")
    public String registerUser(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String confirmPassword,
                               Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error","Passwords do not match");
            return "user/user_register";
        }

        if (userRepo.findByEmail(email) != null) {
            model.addAttribute("error","Email already exists");
            return "user/user_register";
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("USER");

        userRepo.save(user);

        return "redirect:/user/login";
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {

        User user = userRepo.findByEmailAndPassword(email,password);

        if(user != null){
            session.setAttribute("loggedInUser", user);
            return "redirect:/user/dashboard";
        }

        model.addAttribute("error","Invalid email or password");
        return "user/user_login";
    }

    // ================= DASHBOARD =================
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model){

        User user = (User) session.getAttribute("loggedInUser");

        if(user == null){
            return "redirect:/user/login";
        }

        model.addAttribute("userName", user.getName());

        return "user/user_dashboard";
    }

    // ================= PRODUCT LIST =================
    @GetMapping("/products")
    public String products(HttpSession session, Model model){

        if(session.getAttribute("loggedInUser") == null){
            return "redirect:/user/login";
        }

        model.addAttribute("products", productRepo.findAll());

        return "user/user_products";
    }

    // ================= STEP 1: PRODUCT DETAILS =================
    @GetMapping("/product/{id}")
    public String productDetails(@PathVariable Long id,
                                 HttpSession session,
                                 Model model){

        if(session.getAttribute("loggedInUser")==null){
            return "redirect:/user/login";
        }

        Product product = productRepo.findById(id).orElse(null);

        if(product == null){
            return "redirect:/user/products";
        }

        session.setAttribute("lastProductId", id);

        model.addAttribute("product", product);

        // 🔥 STEP 1
        model.addAttribute("step", 1);

        return "user/product_details";
    }

    // ================= UPDATE ADDRESS =================
    @PostMapping("/update-address")
    public String updateAddress(@RequestParam String address,
                                HttpSession session){

        session.setAttribute("userAddress", address);

        return "redirect:/user/product/" + session.getAttribute("lastProductId");
    }

    // ================= STEP 2: PAYMENT PAGE =================
    @GetMapping("/payment")
    public String paymentPage(@RequestParam Long productId,
                              @RequestParam Integer qty,
                              HttpSession session,
                              Model model){

        if(session.getAttribute("loggedInUser")==null){
            return "redirect:/user/login";
        }

        Product product = productRepo.findById(productId).orElse(null);

        if(product == null){
            return "redirect:/user/products";
        }

        double total = product.getPrice() * qty;

        model.addAttribute("product", product);
        model.addAttribute("quantity", qty);
        model.addAttribute("total", total);

        // 🔥 STEP 2
        model.addAttribute("step", 2);

        return "user/payment";
    }

    // ================= STEP 3: PLACE ORDER =================
    @PostMapping("/pay")
    public String placeOrder(@RequestParam(required = false) Long productId,
                             @RequestParam(required = false) Integer quantity,
                             @RequestParam(required = false) String paymentMethod,
                             HttpSession session){

        try {

            User user = (User) session.getAttribute("loggedInUser");

            if(user == null){
                return "redirect:/user/login";
            }

            if(productId == null || quantity == null || paymentMethod == null){
                return "redirect:/user/products";
            }

            Product product = productRepo.findById(productId).orElse(null);

            if(product == null){
                return "redirect:/user/products";
            }

            // 🔥 CREATE ORDER
            Order order = new Order();

            order.setUser(user);
            order.setProduct(product);
            order.setQuantity(quantity);
            order.setTotalPrice(product.getPrice() * quantity);
            order.setPaymentMethod(paymentMethod);
            order.setStatus("PAID");
            order.setOrderDate(LocalDate.now());

            orderRepo.save(order);

            // 🔥 CREATE BILL
            Billing bill = new Billing();

            bill.setOrder(order);
            bill.setCustomer(null);

            bill.setSubTotal(order.getTotalPrice());
            bill.setTaxAmount(0.0);
            bill.setDiscount(0.0);
            bill.setGrandTotal(order.getTotalPrice());

            bill.setPaymentMode(paymentMethod);
            bill.setPaymentStatus("Paid");

            bill.setBillDate(LocalDate.now());
            bill.setBillNumber("BILL-" + UUID.randomUUID()
                    .toString().substring(0,6).toUpperCase());

            billingRepo.save(bill);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/user/orders";
    }

    // ================= USER ORDERS =================
    @GetMapping("/orders")
    public String orders(HttpSession session, Model model){

        User user = (User) session.getAttribute("loggedInUser");

        if(user == null){
            return "redirect:/user/login";
        }

        model.addAttribute("orders", orderRepo.findByUser(user));

        return "user/my_orders";
    }

    // ================= ORDER DETAILS =================
    @GetMapping("/order/view/{id}")
    public String viewOrderDetails(@PathVariable Long id,
                                   HttpSession session,
                                   Model model){

        User user = (User) session.getAttribute("loggedInUser");

        if(user == null){
            return "redirect:/user/login";
        }

        Order order = orderRepo.findById(id).orElse(null);

        if(order == null){
            return "redirect:/user/orders";
        }

        model.addAttribute("order", order);
        model.addAttribute("address", session.getAttribute("userAddress"));

        return "user/order_details";
    }

    // ================= LOGOUT =================
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/user/login";
    }
}