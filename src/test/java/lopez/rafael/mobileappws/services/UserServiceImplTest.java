package lopez.rafael.mobileappws.services;

import lopez.rafael.mobileappws.data.entities.Address;
import lopez.rafael.mobileappws.data.entities.User;
import lopez.rafael.mobileappws.data.repositories.UserRepository;
import lopez.rafael.mobileappws.models.dtos.AddressDto;
import lopez.rafael.mobileappws.models.dtos.UserDto;
import lopez.rafael.mobileappws.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {
    @Mock
    UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    private String encryptedPassword = "bbbb";
    private User user;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        user = new User();
        user.setId(1);
        user.setFirstName("Rafael");
        user.setLastName("Lopez");
        user.setUserId("aaaaa");
        user.setEmail("test@test.com");
        user.setEncryptedPassword(encryptedPassword);
        user.setAddresses(getAddresses());
    }

    @Test
    public final void testGetUser() {
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

    @Test
    public void createUserMethodTest() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setFirstName("Rafael");
        userDto.setLastName("Lopez");
        userDto.setPassword("123");
        userDto.setEmail("test@test.com");

        UserDto storedUserDetails = userService.createUser(userDto);

        assertNotNull(storedUserDetails);
        assertEquals(user.getFirstName(), storedUserDetails.getFirstName());
        assertEquals(user.getLastName(), storedUserDetails.getLastName());
        assertEquals(user.getEmail(), storedUserDetails.getEmail());
        assertNotNull(storedUserDetails.getUserId());
        assertEquals(user.getAddresses().size(), storedUserDetails.getAddresses().size());
        verify(userRepository, times(1)).save(any(User.class));
        verify(bCryptPasswordEncoder, times(1)).encode("123");
    }

    private List<AddressDto> getAddressesDto() {
        AddressDto addressDto = new AddressDto();
        addressDto.setType("Shipping");
        addressDto.setCity("Kingston");
        addressDto.setCountry("Canada");
        addressDto.setPostalCode("ABC123");
        addressDto.setStreetName("123 Street");

        AddressDto billingAddress = new AddressDto();
        billingAddress.setType("Billing");
        billingAddress.setCity("Kingston");
        billingAddress.setCountry("Canada");
        billingAddress.setPostalCode("ABC123");
        billingAddress.setStreetName("123 Street");

        List<AddressDto> addresses = new ArrayList();
        addresses.add(addressDto);
        addresses.add(billingAddress);

        return addresses;
    }

    private List<Address> getAddresses() {
        List<AddressDto> addresses = getAddressesDto();

        Type listType = new TypeToken< List<Address> >() {}.getType();

        return new ModelMapper().map(addresses, listType);
    }
}
