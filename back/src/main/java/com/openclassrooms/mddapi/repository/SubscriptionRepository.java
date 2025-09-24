package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.model.Subscription;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.model.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUserAndTheme(User user, Theme theme);

    List<Subscription> findByUser(User user);

    @Query("SELECT s.theme.id FROM Subscription s WHERE s.user = :user")
    List<Long> findThemeIdsByUser(@Param("user") User user);

    boolean existsByUserAndTheme(User user, Theme theme);

    void deleteByUserAndTheme(User user, Theme theme);
}