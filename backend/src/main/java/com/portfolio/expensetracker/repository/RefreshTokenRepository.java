package com.portfolio.expensetracker.repository;

import com.portfolio.expensetracker.model.RefreshToken;
import com.portfolio.expensetracker.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByUser(User user);

    void deleteAllByUser(User user);
}
