package com.piyas.Service;

import com.piyas.model.IngredientsItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FoodServiceImpTest {

    @Autowired
    private IngredientsService ingredientsService;

    @Test
    public void testCreateIngredientItems() throws Exception {
        IngredientsItem ingredient1 = new IngredientsItem();
        ingredient1.setName("Flour");

        IngredientsItem ingredient2 = new IngredientsItem();
        ingredient2.setName("Chocolate");

        List<IngredientsItem> ingredients = List.of(ingredient1, ingredient2);

        for (IngredientsItem item : ingredients) {
            assertNotNull(item.getName());
        }
    }
}
