package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Supplier;
import com.example.demo.repository.SupplierRepository;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired
    private SupplierRepository supplierRepository;

    // ================= LIST SUPPLIERS =================
    @GetMapping
    public String listSuppliers(Model model) {

        model.addAttribute("suppliers", supplierRepository.findAll());
        model.addAttribute("pageTitle", "Suppliers");
        model.addAttribute("content", "supplier_list");

        return "layout";
    }

    // ================= ADD SUPPLIER PAGE =================
    @GetMapping("/add")
    public String showAddForm(Model model) {

        model.addAttribute("supplier", new Supplier());
        model.addAttribute("pageTitle", "Add Supplier");
        model.addAttribute("content", "add_supplier");

        return "layout";
    }

    // ================= SAVE SUPPLIER =================
    @PostMapping("/save")
    public String saveSupplier(@ModelAttribute Supplier supplier) {

        supplierRepository.save(supplier);

        return "redirect:/suppliers";
    }

    // ================= DELETE SUPPLIER =================
    @GetMapping("/delete/{id}")
    public String deleteSupplier(@PathVariable Long id, Model model) {

        try {

            Supplier supplier = supplierRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));

            supplierRepository.delete(supplier);

        } catch (Exception e) {

            System.out.println("Delete Error: " + e.getMessage());
        }

        return "redirect:/suppliers";
    }
}