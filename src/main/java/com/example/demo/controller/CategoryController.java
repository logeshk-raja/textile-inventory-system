package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Category;
import com.example.demo.repository.CategoryRepository;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    // ================= LIST CATEGORIES =================
    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());

        // 🔥 IMPORTANT FOR LAYOUT
        model.addAttribute("pageTitle", "Categories");
        model.addAttribute("content", "category_list");

        return "layout";   // ✅ USE LAYOUT
    }

    // ================= ADD CATEGORY PAGE =================
    @GetMapping("/add")
    public String addCategoryForm(Model model) {
        model.addAttribute("category", new Category());

        // 🔥 IMPORTANT FOR LAYOUT
        model.addAttribute("pageTitle", "Add Category");
        model.addAttribute("content", "add_category");

        return "layout";   // ✅ USE LAYOUT
    }

    // ================= SAVE CATEGORY =================
    @PostMapping("/save")
    public String saveCategory(@ModelAttribute Category category) {

        System.out.println("Category name = " + category.getName()); // 🔍 DEBUG

        categoryRepository.save(category);
        return "redirect:/categories";
    }
}