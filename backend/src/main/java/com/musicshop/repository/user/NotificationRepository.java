package com.musicshop.repository.user;

import com.musicshop.model.user.Notification;
import com.musicshop.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(User user);

    List<Notification> findByUserOrderByTimestampDesc(User user);
    List<Notification> findByUserIdOrderByTimestampDesc(Long userId);

    List<Notification> findByUserAndIsReadFalse(User user);
    List<Notification> findByUserIdAndIsReadFalse(Long userId);

    long countByUserAndIsReadFalse(User user);
}
