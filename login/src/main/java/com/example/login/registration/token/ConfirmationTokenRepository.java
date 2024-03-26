package com.example.login.registration.token;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long>{
    
    /*
     * Returns Optional<ConfirmationToken> through string token.
     */
    Optional<ConfirmationToken> findByToken(String token);

    /*
     * Deletes ConfirmationToken through ID.
     */
    void deleteById(Long id);

    /*
     * Updates Confirmation token's confirmation time.
     */
    
    @Modifying
    @Query("UPDATE ConfirmationToken c " +
            "SET c.confirmationTime = ?2 " +
            "WHERE c.token = ?1")
    int updateConfirmationTime(String token,
                          LocalDateTime confirmationTime);

    @Modifying
    @Query(value = "DELETE FROM Confirmation_Token c WHERE c.expiration_time < :date AND c.confirmation_time IS NULL", nativeQuery = true)
    int deleteExpirations(@Param("date") LocalDateTime date);
}
