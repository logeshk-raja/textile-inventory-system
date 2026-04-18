package com.example.demo.controller;

import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Product;
import com.example.demo.entity.Category;
import com.example.demo.entity.Supplier;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.SupplierRepository;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SupplierRepository supplierRepository;


    // ================= LIST PRODUCTS =================
    @GetMapping
    public String listProducts(Model model){

        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("pageTitle", "Products");
        model.addAttribute("content", "product_list");

        return "layout";
    }


    // ================= ADD PRODUCT FORM =================
    @GetMapping("/add")
    public String addProductForm(Model model){

        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("suppliers", supplierRepository.findAll());

        model.addAttribute("pageTitle", "Add Product");
        model.addAttribute("content", "add_product");

        return "layout";
    }


    // ================= SAVE PRODUCT =================
    @PostMapping("/save")
    public String saveProduct(
            @RequestParam String productName,
            @RequestParam Integer quantity,
            @RequestParam Double price,
            @RequestParam Long categoryId,
            @RequestParam Long supplierId
    ){

        Category category = categoryRepository.findById(categoryId).orElse(null);
        Supplier supplier = supplierRepository.findById(supplierId).orElse(null);

        if(category == null || supplier == null){
            System.out.println("Category or Supplier not found");
            return "redirect:/products/add";
        }

        Product product = new Product();

        product.setProductName(productName);
        product.setQuantity(quantity);
        product.setPrice(price);
        product.setCategory(category);
        product.setSupplier(supplier);

        productRepository.save(product);

        return "redirect:/products";
    }


    // ================= EDIT PRODUCT =================
    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model){

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("suppliers", supplierRepository.findAll());

        model.addAttribute("pageTitle", "Edit Product");
        model.addAttribute("content", "edit_product");

        return "layout";
    }


    // ================= DELETE PRODUCT =================
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id){

        try {
            productRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Delete Error: " + e.getMessage());
        }

        return "redirect:/products";
    }

    // ================= DOWNLOAD CSV =================
    @GetMapping("/download")
    public void downloadProducts(HttpServletResponse response) throws Exception{

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=products.csv");

        List<Product> products = productRepository.findAll();

        PrintWriter writer = response.getWriter();

        writer.println("ID,Product Name,Category,Quantity,Price,Supplier");

        for(Product p : products){

            writer.println(
                    p.getId() + "," +
                    p.getProductName() + "," +
                    (p.getCategory()!=null ? p.getCategory().getName() : "") + "," +
                    p.getQuantity() + "," +
                    p.getPrice() + "," +
                    (p.getSupplier()!=null ? p.getSupplier().getName() : "")
            );
        }

        writer.flush();
        writer.close();
    }
}