package SubastasMax.auction_service.controllers;

import SubastasMax.auction_service.dto.CategoryRequestDTO;
import SubastasMax.auction_service.dto.CategoryResponseDTO;
import SubastasMax.auction_service.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO requestDTO) 
            throws ExecutionException, InterruptedException {
        CategoryResponseDTO response = categoryService.createCategory(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable String categoryId,
            @RequestBody CategoryRequestDTO requestDTO) 
            throws ExecutionException, InterruptedException {
        CategoryResponseDTO response = categoryService.updateCategory(categoryId, requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable String categoryId) 
            throws ExecutionException, InterruptedException {
        CategoryResponseDTO response = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() 
            throws ExecutionException, InterruptedException {
        List<CategoryResponseDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByUserId(@PathVariable String userId) 
            throws ExecutionException, InterruptedException {
        List<CategoryResponseDTO> categories = categoryService.getCategoriesByUserId(userId);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByAuctionId(@PathVariable String auctionId) 
            throws ExecutionException, InterruptedException {
        List<CategoryResponseDTO> categories = categoryService.getCategoriesByAuctionId(auctionId);
        return ResponseEntity.ok(categories);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String categoryId) 
            throws ExecutionException, InterruptedException {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}