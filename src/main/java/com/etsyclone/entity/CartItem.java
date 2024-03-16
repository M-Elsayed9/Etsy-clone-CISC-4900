package com.etsyclone.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.common.base.Objects;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Index;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_item", indexes = {
        @Index(name = "idx_cart_id", columnList = "cart_id")
})
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "cart_id", nullable = false, updatable = false)
    @JsonBackReference
    private Cart cart;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    private Product product;

    @Column(name = "quantity", nullable = false, columnDefinition = "SMALLINT")
    private Short quantity;

    public CartItem() {
    }

    public CartItem(Cart cart, Product product, Short quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }


    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Short getQuantity() {
        return quantity;
    }

    public void setQuantity(Short quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }

    public Long getCartId() {
        return cart.getId();
    }

    public Long getProductId() {
        return product.getId();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CartItem{");
        sb.append("id=").append(id);
        sb.append(", cartId=").append(cart != null ? cart.getId() : "null");
        sb.append(", productId=").append(product != null ? product.getId() : "null");
        sb.append(", quantity=").append(quantity);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equal(getCart().getId(), cartItem.getCart().getId())
                && Objects.equal(getProduct().getId(), cartItem.getProduct().getId());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getCart().getId());
        result = 31 * result + Objects.hashCode(getProduct().getId());
        return result;
    }


    public BigDecimal getTotalPrice() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
