package lopez.rafael.mobileappws.data.repositories;

import lopez.rafael.mobileappws.data.entities.Address;
import lopez.rafael.mobileappws.data.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends CrudRepository<Address, Long> {
    List<Address> findAllByUserDetails(User user);
    Address findByAddressId(String addressId);
}
