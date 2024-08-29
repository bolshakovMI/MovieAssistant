package com.example.movieAssistant.model.db.repository;

import com.example.movieAssistant.model.db.entity.Friendship;
import com.example.movieAssistant.model.db.entity.UserInfo;
import com.example.movieAssistant.model.enums.RequestConfirmationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface FriendRepo  extends JpaRepository<Friendship, Long> {
    @Query("SELECT CASE WHEN EXISTS (SELECT 1 FROM Friendship f WHERE (f.user1.id = :id1 AND f.user2.id = :id2) OR (f.user1.id = :id2 AND f.user2.id = :id1)) THEN TRUE ELSE FALSE END")
    boolean existsFriendshipBetween(@Param("id1") Long id1, @Param("id2") Long id2);

    @Query("SELECT f FROM Friendship f WHERE (f.user1.id = :id1 AND f.user2.id = :id2) OR (f.user1.id = :id2 AND f.user2.id = :id1)")
    Optional<Friendship> findFriendshipBetween(@Param("id1") Long id1, @Param("id2") Long id2);

    @Query("SELECT f FROM Friendship f WHERE " +
            "(f.user1.id = :userId OR f.user2.id = :userId) AND " +
            "f.withdrawn = :isWithdrawn AND " +
            "f.status = :status")
    Page<Friendship> findByUserIdAndWithdrawnAndStatus(
            @Param("userId") long userId,
            @Param("isWithdrawn") boolean isWithdrawn,
            @Param("status") RequestConfirmationStatus status,
            Pageable pageable);

    Page<Friendship> findAllByStatusAndWithdrawn(RequestConfirmationStatus status, boolean isWithdrawn, Pageable pageable);

    Page<Friendship> findByUser1AndStatusAndWithdrawn(UserInfo userInfo, RequestConfirmationStatus status, boolean isWithdrawn, Pageable pageable);

    Page<Friendship> findByUser2AndStatusAndWithdrawn(UserInfo userInfo, RequestConfirmationStatus status, boolean isWithdrawn, Pageable pageable);
}
