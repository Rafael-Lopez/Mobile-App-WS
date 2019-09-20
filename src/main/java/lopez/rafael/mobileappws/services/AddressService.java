package lopez.rafael.mobileappws.services;

import lopez.rafael.mobileappws.models.dtos.AddressDto;

import java.util.List;

public interface AddressService {
    List<AddressDto> getAddresses(String userId);
}
