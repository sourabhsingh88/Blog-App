package com.sourabh.authservice.repository;

import com.sourabh.authservice.entity.Otp;
import com.sourabh.authservice.enums.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    @Query("""
        SELECT o
        FROM Otp o
        WHERE o.email = :email
          AND o.type = :type
          AND o.verified = false
          AND o.expiry > :now
        ORDER BY o.createdAt DESC
    """)
    Optional<Otp> findValidEmailOtp(
            @Param("email") String email,
            @Param("type") OtpType type,
            @Param("now") LocalDateTime now
    );

    @Query("""
        SELECT o
        FROM Otp o
        WHERE o.phone = :phone
          AND o.type = :type
          AND o.verified = false
          AND o.expiry > :now
        ORDER BY o.createdAt DESC
    """)
    Optional<Otp> findValidPhoneOtp(
            @Param("phone") String phone,
            @Param("type") OtpType type,
            @Param("now") LocalDateTime now
    );
}
