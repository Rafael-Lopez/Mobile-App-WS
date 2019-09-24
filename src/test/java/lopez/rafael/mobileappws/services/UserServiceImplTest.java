package lopez.rafael.mobileappws.services;

import lopez.rafael.mobileappws.data.entities.User;
import lopez.rafael.mobileappws.data.repositories.UserRepository;
import lopez.rafael.mobileappws.models.dtos.UserDto;
import lopez.rafael.mobileappws.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

public class UserServiceImplTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public final void testGetUser() {
        User user = new User();
        user.setId(1);
        user.setFirstName("Rafael");
        user.setLastName("Lopez");
        user.setUserId("aaaaa");
        user.setEncryptedPassword("bbbbb");

        when(userRepository.findByEmail(anyString())).thenReturn(user);

        UserDto userDto = userService.getUser("test@test.com");

        assertNotNull(userDto);
        assertEquals("Rafael", userDto.getFirstName());
    }

    @Test
    public void testGetUserNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> { userService.getUser("test@test.com"); } );
    }
}
