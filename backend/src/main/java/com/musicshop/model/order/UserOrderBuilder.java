package com.musicshop.model.order;

import com.musicshop.model.address.Address;
import com.musicshop.model.product.Product;
import com.musicshop.model.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class UserOrderBuilder {

    private User user;
    private LocalDateTime orderDate;
    private Address shippingAddress;
    private BigDecimal totalAmount;
    private String status = "PENDING";
    private final Set<OrderDetail> orderDetails = new HashSet<>();

    UserOrderBuilder() {
    }

    public UserOrderBuilder user(User user) {
        this.user = user;
        return this;
    }

    public UserOrderBuilder orderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
        return this;
    }

    public UserOrderBuilder shippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
        return this;
    }

    public UserOrderBuilder totalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public UserOrderBuilder status(String status) {
        this.status = status;
        return this;
    }

    public UserOrderBuilder addOrderDetail(Product product, int quantity, BigDecimal priceAtPurchase) {
        OrderDetail detail = new OrderDetail();
        detail.setProduct(product);
        detail.setQuantity(quantity);
        detail.setPriceAtPurchase(priceAtPurchase);
        orderDetails.add(detail);
        return this;
    }

    public UserOrder build() {
        if (user == null) {
            throw new IllegalStateException("User is required to build an order");
        }
        if (orderDetails.isEmpty()) {
            throw new IllegalStateException("At least one order detail is required");
        }

        UserOrder order = new UserOrder();
        order.setUser(user);
        order.setOrderDate(orderDate != null ? orderDate : LocalDateTime.now());
        order.setShippingAddress(shippingAddress);
        order.setStatus(status);

        if (totalAmount != null) {
            order.setTotalAmount(totalAmount);
        } else {
            BigDecimal computed = orderDetails.stream()
                    .map(d -> d.getPriceAtPurchase().multiply(BigDecimal.valueOf(d.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setTotalAmount(computed);
        }

        for (OrderDetail detail : orderDetails) {
            detail.setOrder(order);
        }
        order.setOrderDetails(orderDetails);

        return order;
    }
}
