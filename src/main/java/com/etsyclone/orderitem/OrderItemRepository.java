package com.etsyclone.orderitem;

import com.etsyclone.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    void deleteByOrder_Id(Long orderId);

    void deleteByProduct_Id(Long productId);

    Set<Order> findByOrder_Id(Long id);
}
