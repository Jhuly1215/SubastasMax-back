package SubastasMax.auction_service.controllers;

import SubastasMax.auction_service.dto.ProductRequestDto;
import SubastasMax.auction_service.dto.ProductResponseDto;
import SubastasMax.auction_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto productRequestDto) 
            throws ExecutionException, InterruptedException {
        ProductResponseDto product = productService.createProduct(productRequestDto);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable String productId,
            @RequestBody ProductRequestDto productRequestDto) 
            throws ExecutionException, InterruptedException {
        ProductResponseDto product = productService.updateProduct(productId, productRequestDto);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable String productId) 
            throws ExecutionException, InterruptedException {
        ProductResponseDto product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() 
            throws ExecutionException, InterruptedException {
        List<ProductResponseDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByUserId(@PathVariable String userId) 
            throws ExecutionException, InterruptedException {
        List<ProductResponseDto> products = productService.getProductsByUserId(userId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByAuctionId(@PathVariable String auctionId) 
            throws ExecutionException, InterruptedException {
        List<ProductResponseDto> products = productService.getProductsByAuctionId(auctionId);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId) 
            throws ExecutionException, InterruptedException {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}

