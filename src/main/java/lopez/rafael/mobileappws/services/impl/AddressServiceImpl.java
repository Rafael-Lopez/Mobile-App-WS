package lopez.rafael.mobileappws.services.impl;

import lopez.rafael.mobileappws.data.entities.Address;
import lopez.rafael.mobileappws.data.entities.User;
import lopez.rafael.mobileappws.data.repositories.AddressRepository;
import lopez.rafael.mobileappws.data.repositories.UserRepository;
import lopez.rafael.mobileappws.models.dtos.AddressDto;
import lopez.rafael.mobileappws.services.AddressService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    private UserRepository userRepository;
    private AddressRepository addressRepository;

    @Autowired
    public AddressServiceImpl(UserRepository userRepository, AddressRepository addressRepository){
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public List<AddressDto> getAddresses(String userId) {
        List<AddressDto> returnValue = new ArrayList();
        ModelMapper modelMapper = new ModelMapper();

        User user = userRepository.findByUserId(userId);

        if(user != null) {
            Iterable<Address> addresses = addressRepository.findAllByUserDetails(user);
            for(Address address : addresses) {
                returnValue.add( modelMapper.map(address, AddressDto.class) );
            }
        }

        return returnValue;
    }

    @Override
    public AddressDto getAddress(String addressId) {
        AddressDto returnValue = null;

        Address address = addressRepository.findByAddressId(addressId);

        if(address != null) {
            returnValue = new ModelMapper().map(address, AddressDto.class);
        }

        return returnValue;
    }
}
