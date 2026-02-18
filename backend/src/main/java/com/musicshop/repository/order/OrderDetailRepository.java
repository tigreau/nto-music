package com.musicshop.repository.order;

import com.musicshop.model.order.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    boolean existsByProductId(Long productId);
}
