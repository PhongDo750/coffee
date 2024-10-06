package org.example.coffee.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.example.coffee.dto.category.CategoryOutput;
import org.example.coffee.dto.product.ProductInput;
import org.example.coffee.dto.product.ProductOutput;
import org.example.coffee.dto.productCategoryMap.ProductCategoryMapRequest;
import org.example.coffee.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/product")
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "thêm vào sản phẩm")
    @PostMapping("/create")
    public void createProduct(@RequestHeader("Authorization") String accessToken,
                              @RequestBody ProductInput productInput) {
        productService.createProduct(accessToken, productInput);
    }

    @Operation(summary = "Sửa sản phẩm")
    @PostMapping("/update")
    public void updateProduct(@RequestHeader("Authorization") String accessToken,
                              @RequestBody ProductInput productInput,
                              @RequestParam Long productId) {
        productService.updateProduct(accessToken, productInput, productId);
    }

    @Operation(summary = "Xóa sản phẩm")
    @DeleteMapping("/delete")
    public void deleteProduct(@RequestHeader("Authorization") String accessToken,
                              @RequestParam Long productId) {
        productService.deleteProduct(accessToken, productId);
    }

    @Operation(summary = "Lấy ra sản phẩm của shop")
    @GetMapping("/get-products")
    public List<ProductOutput> getProducts() {
        return productService.getProducts();
    }

    @PostMapping("/add-to-category")
    public ResponseEntity<String> addProductToCategory(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody ProductCategoryMapRequest productCategoryMapRequest) {
        productService.addProductToCategory(
                accessToken,
                productCategoryMapRequest);
        return ResponseEntity.ok("Product added to category successfully");
    }

    @DeleteMapping("/remove-from-category")
    public ResponseEntity<String> removeProductFromCategory(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody ProductCategoryMapRequest productCategoryMapRequest) {
        productService.removeProductFromCategory(
                accessToken,
                productCategoryMapRequest);
        return ResponseEntity.ok("Product removed from category successfully");
    }

    @GetMapping("/{productId}/categories")
    public ResponseEntity<List<CategoryOutput>> getCategoriesByProduct(@PathVariable Long productId) {
        List<CategoryOutput> categories = productService.getCategoriesByProduct(productId);
        return ResponseEntity.ok(categories);
    }
}
