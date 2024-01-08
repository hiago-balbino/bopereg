package br.com.wes.repositories;

import br.com.wes.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM users WHERE u.userName = :userName")
    public User findByUsername(@Param("userName") String userName);

}
