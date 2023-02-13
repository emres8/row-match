package com.emres.auth;


import com.emres.model.User;

import com.emres.repository.UserRepository;
import com.emres.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationServiceTest {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @Test
    public void testRegister_Success() {
        userService.clearAll();
        RegisterRequest registerRequest = new RegisterRequest("Test", "test@example.com", 1, 5000, "password");
        AuthenticationResponse response = authenticationService.register(registerRequest);
        Optional<User> user = userRepository.findByEmail("test@example.com");
        User nonEmptyUser = user.get();
        Assertions.assertNotNull(user);
        Assertions.assertNotNull(response.getToken());
        Assertions.assertEquals("Test", nonEmptyUser.getName());
        Assertions.assertEquals("test@example.com", nonEmptyUser.getEmail());
        Assertions.assertEquals(1, nonEmptyUser.getLevel());
        Assertions.assertEquals(5000, nonEmptyUser.getCoin());
        Assertions.assertTrue(passwordEncoder.matches("password", nonEmptyUser.getPassword()));
    }

    @Test
    public void testAuthenticate_Success() {
        userService.clearAll();
        RegisterRequest registerRequest = new RegisterRequest("Test", "test@example.com", 1, 5000, "password");
        authenticationService.register(registerRequest);
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("test@example.com", "password");
        AuthenticationResponse response = authenticationService.authenticate(authenticationRequest);
        Assertions.assertNotNull(response.getToken());
    }

}
