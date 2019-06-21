package lopez.rafael.mobileappws.data.repositories;

import lopez.rafael.mobileappws.data.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
