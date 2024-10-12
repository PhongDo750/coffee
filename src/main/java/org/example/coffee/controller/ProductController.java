package org.example.coffee.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.example.coffee.dto.product.ProductInput;
import org.example.coffee.dto.product.ProductOutput;
import org.example.coffee.service.ProductService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
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
    public Page<ProductOutput> getProducts(Pageable pageable) {
        return productService.getProducts(pageable);
    }

    @Operation(summary = "Lấy sản phẩm theo category")
    @GetMapping("/get-products-by-category")
    public Page<ProductOutput> getProductsByCategory(@RequestParam Long categoryId,
                                                    @ParameterObject Pageable pageable) {
        return productService.getProductsByCategory(pageable, categoryId);
    }

    @Operation(summary = "Thêm sản phẩm vào danh mục")
    @PostMapping("/add-product-to-category")
    public void addProductToCategory(@RequestHeader("Authorization") String accessToken,
                                     @RequestParam Long productId,
                                     @RequestParam Long categoryId) {
        productService.addProductToCategory(accessToken, productId, categoryId);
    }

    @Operation(summary = "Xem chi tiết sản phẩm")
    @GetMapping("/get-details")
    public ProductOutput getProductDetails(@RequestParam Long productId) {
        return productService.getProductDetails(productId);
    }

    @Operation(summary = "Xóa sản phẩm trong category")
    @DeleteMapping("/delete-product-from-category")
    public void deleteProductFromCategory(@RequestHeader("Authorization") String accessToken,
                                          @RequestParam Long categoryId,
                                          @RequestParam Long productId) {
        productService.removeProductFromCategory(accessToken, categoryId, productId);
    }
}
