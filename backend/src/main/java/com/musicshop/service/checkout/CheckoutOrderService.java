package com.musicshop.service.checkout;

import com.musicshop.model.address.Address;
import com.musicshop.model.cart.CartDetail;
import com.musicshop.model.order.UserOrder;
import com.musicshop.model.order.UserOrderBuilder;
import com.musicshop.model.payment.Payment;
import com.musicshop.model.user.User;
import com.musicshop.repository.order.OrderRepository;
import com.musicshop.repository.payment.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckoutOrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public CheckoutOrderService(OrderRepository orderRepository, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    public UserOrder buildOrder(User user, Address shippingAddress, List<CartDetail> cartDetails, BigDecimal totalAmount) {
        UserOrderBuilder builder = UserOrder.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .shippingAddress(shippingAddress)
                .totalAmount(totalAmount);

        for (CartDetail cartDetail : cartDetails) {
            builder.addOrderDetail(
                    cartDetail.getProduct(),
                    cartDetail.getQuantity(),
                    cartDetail.getProduct().getPrice()
            );
        }

        return builder.build();
    }

    public UserOrder saveConfirmedOrder(UserOrder order) {
        order.setStatus("CONFIRMED");
        return orderRepository.save(order);
    }

    public void recordPayment(UserOrder order, String paymentMethod, BigDecimal totalAmount) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setAmount(totalAmount);
        paymentRepository.save(payment);
    }
}
