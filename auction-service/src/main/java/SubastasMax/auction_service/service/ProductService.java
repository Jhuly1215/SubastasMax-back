package SubastasMax.auction_service.service;

import SubastasMax.auction_service.dto.ProductRequestDto;
import SubastasMax.auction_service.dto.ProductResponseDto;
import SubastasMax.auction_service.exception.ProductNotFoundException;
import SubastasMax.auction_service.model.Product;
import SubastasMax.auction_service.repository.ProductRepository;
import com.google.cloud.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public ProductResponseDto createProduct(ProductRequestDto requestDto) 
            throws ExecutionException, InterruptedException {
        Product product = new Product();
        product.setName(requestDto.getName());
        product.setDescription(requestDto.getDescription());
        product.setCategorys(requestDto.getCategorys());
        product.setCondition(requestDto.getCondition() != null ? requestDto.getCondition() : "new");
        product.setImages(requestDto.getImages() != null ? requestDto.getImages() : new ArrayList<>());
        product.setSpecifications(requestDto.getSpecifications());
        product.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : "available");
        product.setCreatedBy(requestDto.getCreatedBy());
        product.setCreatedAt(Timestamp.now());
        product.setUsedInAuctions(requestDto.getUsedInAuctions() != null ? requestDto.getUsedInAuctions() : new ArrayList<>());

        String productId = productRepository.save(product);
        product.setId(productId);

        return mapToResponseDto(product);
    }

    public ProductResponseDto updateProduct(String productId, ProductRequestDto requestDto) 
            throws ExecutionException, InterruptedException {
        Product existingProduct = productRepository.findById(productId);
        
        if (existingProduct == null) {
            throw new ProductNotFoundException("Product not found with id: " + productId);
        }

        if (requestDto.getName() != null) {
            existingProduct.setName(requestDto.getName());
        }
        if (requestDto.getDescription() != null) {
            existingProduct.setDescription(requestDto.getDescription());
        }
        if (requestDto.getCategorys() != null) {
            existingProduct.setCategorys(requestDto.getCategorys());
        }
        if (requestDto.getCondition() != null) {
            existingProduct.setCondition(requestDto.getCondition());
        }
        if (requestDto.getImages() != null) {
            existingProduct.setImages(requestDto.getImages());
        }
        if (requestDto.getSpecifications() != null) {
            existingProduct.setSpecifications(requestDto.getSpecifications());
        }
        if (requestDto.getStatus() != null) {
            existingProduct.setStatus(requestDto.getStatus());
        }
        if (requestDto.getUsedInAuctions() != null) {
            existingProduct.setUsedInAuctions(requestDto.getUsedInAuctions());
        }

        productRepository.save(existingProduct);

        return mapToResponseDto(existingProduct);
    }

    public ProductResponseDto getProductById(String productId) 
            throws ExecutionException, InterruptedException {
        Product product = productRepository.findById(productId);
        
        if (product == null) {
            throw new ProductNotFoundException("Product not found with id: " + productId);
        }

        return mapToResponseDto(product);
    }

    public List<ProductResponseDto> getAllProducts() 
            throws ExecutionException, InterruptedException {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDto> getProductsByUserId(String userId) 
            throws ExecutionException, InterruptedException {
        List<Product> products = productRepository.findByCreatedBy(userId);
        return products.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDto> getProductsByAuctionId(String auctionId) 
            throws ExecutionException, InterruptedException {
        List<Product> products = productRepository.findByAuctionId(auctionId);
        return products.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public void deleteProduct(String productId) 
            throws ExecutionException, InterruptedException {
        if (!productRepository.exists(productId)) {
            throw new ProductNotFoundException("Product not found with id: " + productId);
        }
        productRepository.delete(productId);
    }

    private ProductResponseDto mapToResponseDto(Product product) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setCategorys(product.getCategorys());
        dto.setCondition(product.getCondition());
        dto.setImages(product.getImages());
        dto.setSpecifications(product.getSpecifications());
        dto.setStatus(product.getStatus());
        dto.setCreatedBy(product.getCreatedBy());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUsedInAuctions(product.getUsedInAuctions());
        return dto;
    }
}