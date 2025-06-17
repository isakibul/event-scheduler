package com.piyas.Service;

import com.piyas.config.JwtProvider;
import com.piyas.model.User;
import com.piyas.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImpTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private UserServiceImp userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindUserByEmail_Success() throws Exception {
        String email = "user@example.com";
        User mockUser = new User();
        mockUser.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(mockUser);

        User result = userService.findUserByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testFindUserByEmail_UserNotFound() {
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> {
            userService.findUserByEmail(email);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testFindUserByJwtToken_Success() throws Exception {
        String jwt = "mock.jwt.token";
        String email = "user@example.com";
        User mockUser = new User();
        mockUser.setEmail(email);

        when(jwtProvider.getEmailFromJwtToken(jwt)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(mockUser);

        User result = userService.findUserByJwtToken(jwt);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(jwtProvider, times(1)).getEmailFromJwtToken(jwt);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testFindUserByJwtToken_UserNotFound() {
        String jwt = "mock.jwt.token";
        String email = "unknown@example.com";

        when(jwtProvider.getEmailFromJwtToken(jwt)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> {
            userService.findUserByJwtToken(jwt);
        });

        assertEquals("User not found", exception.getMessage());
        verify(jwtProvider, times(1)).getEmailFromJwtToken(jwt);
        verify(userRepository, times(1)).findByEmail(email);
    }
}
