package com.demo.app.repository;

import com.demo.app.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query("select t from Token t inner join User u on t.user.id = u.id " +
            " where u.id = :userId and (t.expired = false or t.revoked = false)")
    List<Token> findAllValidTokenByUser(@Param("userId") Integer userId);

    Optional<Token> findByToken(String token);

    Optional<Token> findByTokenAndExpiredFalse(String token);

    @Modifying
    @Query("delete from Token t where t.revoked = true ")
    void deleteAllByRevokedTrue();

    @Modifying
    @Query("delete from Token t where t.user.id = :userId")
    void deleteAllByUser(@Param("userId") int id);
}
