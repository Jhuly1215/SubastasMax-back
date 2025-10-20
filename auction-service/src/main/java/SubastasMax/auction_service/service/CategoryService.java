package SubastasMax.auction_service.service;

import SubastasMax.auction_service.dto.CategoryRequestDTO;
import SubastasMax.auction_service.dto.CategoryResponseDTO;
import SubastasMax.auction_service.exception.CategoryNotFoundException;
import SubastasMax.auction_service.model.Category;
import SubastasMax.auction_service.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO) 
            throws ExecutionException, InterruptedException {
        Category category = Category.builder()
                .amount(requestDTO.getAmount())
                .auctionId(requestDTO.getAuctionId())
                .status(requestDTO.getStatus())
                .timestamp(Instant.now())
                .userId(requestDTO.getUserId())
                .userName(requestDTO.getUserName())
                .build();

        String categoryId = categoryRepository.save(category);
        category.setId(categoryId);

        return mapToResponseDTO(category);
    }

    public CategoryResponseDTO updateCategory(String categoryId, CategoryRequestDTO requestDTO) 
            throws ExecutionException, InterruptedException {
        Category existingCategory = categoryRepository.findById(categoryId);
        
        if (existingCategory == null) {
            throw new CategoryNotFoundException("Category not found with id: " + categoryId);
        }

        Category categoryToUpdate = Category.builder()
                .amount(requestDTO.getAmount())
                .auctionId(requestDTO.getAuctionId())
                .status(requestDTO.getStatus())
                .userId(requestDTO.getUserId())
                .userName(requestDTO.getUserName())
                .build();

        categoryRepository.update(categoryId, categoryToUpdate);

        Category updatedCategory = categoryRepository.findById(categoryId);
        return mapToResponseDTO(updatedCategory);
    }

    public CategoryResponseDTO getCategoryById(String categoryId) 
            throws ExecutionException, InterruptedException {
        Category category = categoryRepository.findById(categoryId);
        
        if (category == null) {
            throw new CategoryNotFoundException("Category not found with id: " + categoryId);
        }

        return mapToResponseDTO(category);
    }

    public List<CategoryResponseDTO> getAllCategories() 
            throws ExecutionException, InterruptedException {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<CategoryResponseDTO> getCategoriesByUserId(String userId) 
            throws ExecutionException, InterruptedException {
        List<Category> categories = categoryRepository.findByUserId(userId);
        return categories.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<CategoryResponseDTO> getCategoriesByAuctionId(String auctionId) 
            throws ExecutionException, InterruptedException {
        List<Category> categories = categoryRepository.findByAuctionId(auctionId);
        return categories.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public void deleteCategory(String categoryId) 
            throws ExecutionException, InterruptedException {
        Category category = categoryRepository.findById(categoryId);
        
        if (category == null) {
            throw new CategoryNotFoundException("Category not found with id: " + categoryId);
        }

        categoryRepository.delete(categoryId);
    }

    private CategoryResponseDTO mapToResponseDTO(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .amount(category.getAmount())
                .auctionId(category.getAuctionId())
                .status(category.getStatus())
                .timestamp(category.getTimestamp())
                .userId(category.getUserId())
                .userName(category.getUserName())
                .build();
    }
}