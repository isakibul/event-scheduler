package com.piyas.Service;

import com.piyas.model.*;
import com.piyas.repository.*;
import com.piyas.request.OrderRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private CartService cartService;

    private User testUser;
    private Address testAddress;
    private Restaurant testRestaurant;
    private Cart testCart;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Set up test data
        testAddress = new Address();

        testUser = new User();
        testUser.setId(1L);
        testUser.setAddresses(new ArrayList<>());

        testRestaurant = new Restaurant();
        testRestaurant.setId(1L);
        testRestaurant.setOrders(new ArrayList<>());

        CartItem cartItem = new CartItem();
        cartItem.setFood(new Food());
        cartItem.setIngredients(List.of("Cheese")); // ✅ fixed
        cartItem.setQuantity(2);
        cartItem.setTotalPrice(200L);

        testCart = new Cart();
        testCart.setItems(List.of(cartItem));
    }

    @Test
    void testCreateOrder() throws Exception {
        // Given
        OrderRequest request = new OrderRequest();
        request.setDeliveryAddress(testAddress);
        request.setRestaurantId(1L);

        // Mock behaviors
        when(addressRepository.save(testAddress)).thenReturn(testAddress);
        when(restaurantService.findRestaurantById(1L)).thenReturn(testRestaurant);
        when(cartService.findCartByUserId(1L)).thenReturn(testCart);
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(inv -> inv.getArguments()[0]);
        when(cartService.calculateCartTotals(testCart)).thenReturn(200L);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArguments()[0]);

        // When
        Order createdOrder = orderService.createOrder(request, testUser);

        // Then
        assertNotNull(createdOrder);
        assertEquals("PENDING", createdOrder.getOrderStatus());
        assertEquals(1, createdOrder.getItems().size());
        assertEquals(200L, createdOrder.getTotalPrice());

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }
}
