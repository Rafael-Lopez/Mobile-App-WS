package lopez.rafael.mobileappws.data.repositories;

import lopez.rafael.mobileappws.data.entities.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    User findByEmail(String email);
    User findByUserId(String userId);
}
