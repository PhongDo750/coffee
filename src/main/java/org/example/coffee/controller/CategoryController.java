package org.example.coffee.controller;

import lombok.AllArgsConstructor;
import org.example.coffee.dto.category.CategoryInput;
import org.example.coffee.dto.product.ProductOutput;
import org.example.coffee.service.CategoryService;
import org.example.coffee.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
@AllArgsConstructor
public class CategoryController {
    private CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<String> createCategory (
            @RequestHeader("Authorization") String accessToken,
            @RequestBody CategoryInput categoryInput) {
        categoryService.createCategory(accessToken, categoryInput);
        return ResponseEntity.ok("Category created");
    }

    @GetMapping("/{categoryId}/products")
    public ResponseEntity<List<ProductOutput>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductOutput> products = categoryService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }
}
