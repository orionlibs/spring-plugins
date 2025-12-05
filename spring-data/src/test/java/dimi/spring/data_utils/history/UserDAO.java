package dimi.spring.data_utils.history;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<UserModel, Long>
{
}
