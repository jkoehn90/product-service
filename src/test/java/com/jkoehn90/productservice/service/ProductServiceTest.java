package com.jkoehn90.productservice.service;

import com.jkoehn90.productservice.dto.ProductRequest;
import com.jkoehn90.productservice.dto.ProductResponse;
import com.jkoehn90.productservice.entity.Product;
import com.jkoehn90.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product mockProduct;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        mockProduct = Product.builder()
                .id(1L)
                .name("iPhone 15")
                .description("Apple iPhone 15 128GB")
                .price(799.99)
                .quantity(50)
                .build();

        productRequest = ProductRequest.builder()
                .name("iPhone 15")
                .description("Apple iPhone 15 128GB")
                .price(799.99)
                .quantity(50)
                .build();
    }

    // ─── Create Tests ─────────────────────────────────────────────────────────

    @Test
    void createProduct_ShouldReturnProductResponse_WhenValidRequest() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        // Act
        ProductResponse response = productService.createProduct(productRequest);

        // Assert
        assertNotNull(response);
        assertEquals("iPhone 15", response.getName());
        assertEquals(799.99, response.getPrice());
        assertEquals(50, response.getQuantity());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    // ─── Get All Tests ────────────────────────────────────────────────────────

    @Test
    void getAllProducts_ShouldReturnListOfProducts() {
        // Arrange
        when(productRepository.findAll()).thenReturn(List.of(mockProduct));

        // Act
        List<ProductResponse> responses = productService.getAllProducts();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("iPhone 15", responses.get(0).getName());
    }

    @Test
    void getAllProducts_ShouldReturnEmptyList_WhenNoProducts() {
        // Arrange
        when(productRepository.findAll()).thenReturn(List.of());

        // Act
        List<ProductResponse> responses = productService.getAllProducts();

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    // ─── Get By ID Tests ──────────────────────────────────────────────────────

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        // Act
        ProductResponse response = productService.getProductById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("iPhone 15", response.getName());
    }

    @Test
    void getProductById_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.getProductById(99L));

        assertEquals("Product not found with id: 99", exception.getMessage());
    }

    // ─── Update Tests ─────────────────────────────────────────────────────────

    @Test
    void updateProduct_ShouldReturnUpdatedProduct_WhenProductExists() {
        // Arrange
        ProductRequest updateRequest = ProductRequest.builder()
                .name("iPhone 15 Pro")
                .description("Apple iPhone 15 Pro 256GB")
                .price(999.99)
                .quantity(30)
                .build();

        Product updatedProduct = Product.builder()
                .id(1L)
                .name("iPhone 15 Pro")
                .description("Apple iPhone 15 Pro 256GB")
                .price(999.99)
                .quantity(30)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        ProductResponse response = productService.updateProduct(1L, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("iPhone 15 Pro", response.getName());
        assertEquals(999.99, response.getPrice());
        assertEquals(30, response.getQuantity());
    }

    @Test
    void updateProduct_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.updateProduct(99L, productRequest));

        assertEquals("Product not found with id: 99", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    // ─── Delete Tests ─────────────────────────────────────────────────────────

    @Test
    void deleteProduct_ShouldDeleteProduct_WhenProductExists() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteProduct_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.deleteProduct(99L));

        assertEquals("Product not found with id: 99", exception.getMessage());
        verify(productRepository, never()).deleteById(anyLong());
    }

    // ─── Search Tests ─────────────────────────────────────────────────────────

    @Test
    void searchProducts_ShouldReturnMatchingProducts() {
        // Arrange
        when(productRepository.findByNameContainingIgnoreCase("iphone"))
                .thenReturn(List.of(mockProduct));

        // Act
        List<ProductResponse> responses = productService.searchProducts("iphone");

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("iPhone 15", responses.get(0).getName());
    }

    @Test
    void searchProducts_ShouldReturnEmptyList_WhenNoMatches() {
        // Arrange
        when(productRepository.findByNameContainingIgnoreCase(anyString()))
                .thenReturn(List.of());

        // Act
        List<ProductResponse> responses = productService.searchProducts("nonexistent");

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    // ─── In Stock Tests ───────────────────────────────────────────────────────

    @Test
    void getInStockProducts_ShouldReturnProductsWithQuantityGreaterThanZero() {
        // Arrange
        when(productRepository.findByQuantityGreaterThan(0))
                .thenReturn(List.of(mockProduct));

        // Act
        List<ProductResponse> responses = productService.getInStockProducts();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertTrue(responses.get(0).getQuantity() > 0);
    }
}
