package com.etsyclone.order;

import com.etsyclone.address.Address;
import com.etsyclone.orderitem.OrderItem;
import com.etsyclone.user.User;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.common.base.Objects;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customer_order")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User customer;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false, updatable = false)
    private Address address;

    @Column(name = "total_price", nullable = false, columnDefinition = "DECIMAL(10,2)", updatable = false)
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems = new HashSet<>();

    @Column(name = "payment_status", length = 20, columnDefinition = "VARCHAR(20)")
    private String orderStatus;

    public Order(User user, Address address, BigDecimal totalPrice) {
        this.customer = user;
        this.address = address;
        this.totalPrice = totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        if (totalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total price cannot be negative");
        }
        this.totalPrice = totalPrice;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }

    public void updateOrderItem(OrderItem orderItem) {
        orderItems.stream()
                .filter(item -> item.getProduct().getId().equals(orderItem.getProduct().getId()))
                .findFirst()
                .ifPresent(item -> item.setQuantity(orderItem.getQuantity()));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Order{");
        sb.append("id=").append(id);
        sb.append(", customerId=").append(customer != null ? customer.getId() : "null");
        sb.append(", addressId=").append(address != null ? address.getId() : "null");
        sb.append(", totalPrice=").append(totalPrice);
        sb.append(", orderItemsCount=").append(orderItems.size());
        sb.append(", orderStatus='").append(orderStatus).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equal(getCustomer().getId(), order.getCustomer().getId())
                && Objects.equal(getAddress().getId(), order.getAddress().getId())
                && Objects.equal(getTotalPrice(), order.getTotalPrice())
                && Objects.equal(getOrderItems(), order.getOrderItems());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getCustomer().getId());
        result = 31 * result + Objects.hashCode(getAddress().getId());
        result = 31 * result + Objects.hashCode(getTotalPrice());
        result = 31 * result + Objects.hashCode(getOrderItems());
        return result;
    }
}
