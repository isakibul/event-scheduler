package com.piyas.Service;

import com.piyas.model.Category;
import com.piyas.model.Restaurant;
import com.piyas.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CategoryServiceImpTest {

    @Autowired
    private CategoryService categoryService;

    @MockBean
    private RestaurantService restaurantService;

    @MockBean
    private CategoryRepository categoryRepository;

    private Restaurant mockRestaurant;

    @BeforeEach
    void setUp() {
        // Setup a mock Restaurant object to be returned by restaurantService
        mockRestaurant = new Restaurant();
        mockRestaurant.setId(1L);
        mockRestaurant.setName("Test Restaurant");
    }

    @Test
    public void testCreateCategory() throws Exception {
        String categoryName = "Desserts";
        Long userId = 1L;

        // Mock restaurantService behavior
        when(restaurantService.getRestaurantsByUserId(userId)).thenReturn(mockRestaurant);

        // Mock categoryRepository save method
        Category savedCategory = new Category();
        savedCategory.setId(10L);
        savedCategory.setName(categoryName);
        savedCategory.setRestaurant(mockRestaurant);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        Category result = categoryService.createCategory(categoryName, userId);

        assertNotNull(result);
        assertEquals(categoryName, result.getName());
        assertEquals(mockRestaurant, result.getRestaurant());

        verify(restaurantService, times(1)).getRestaurantsByUserId(userId);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    public void testFindCategoryByRestaurantId() throws Exception {
        Long userId = 1L;

        // Mock restaurantService to return restaurant with id=1
        when(restaurantService.getRestaurantsByUserId(userId)).thenReturn(mockRestaurant);

        // Mock category list returned by repository
        Category cat1 = new Category();
        cat1.setId(1L);
        cat1.setName("Category1");
        cat1.setRestaurant(mockRestaurant);

        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setName("Category2");
        cat2.setRestaurant(mockRestaurant);

        when(categoryRepository.findByRestaurantId(mockRestaurant.getId()))
                .thenReturn(List.of(cat1, cat2));

        List<Category> categories = categoryService.findCategoryByRestaurantId(userId);

        assertNotNull(categories);
        assertEquals(2, categories.size());
        assertEquals("Category1", categories.get(0).getName());
        assertEquals("Category2", categories.get(1).getName());

        verify(restaurantService, times(1)).getRestaurantsByUserId(userId);
        verify(categoryRepository, times(1)).findByRestaurantId(mockRestaurant.getId());
    }

    @Test
    public void testFindCategoryById_Success() throws Exception {
        Long categoryId = 5L;

        Category cat = new Category();
        cat.setId(categoryId);
        cat.setName("Beverages");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(cat));

        Category result = categoryService.findCategoryById(categoryId);

        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        assertEquals("Beverages", result.getName());

        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    public void testFindCategoryById_NotFound() {
        Long categoryId = 99L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            categoryService.findCategoryById(categoryId);
        });

        assertEquals("Category not found ", exception.getMessage());

        verify(categoryRepository, times(1)).findById(categoryId);
    }
}
