package com.musicshop.service.checkout;

import com.musicshop.dto.checkout.CheckoutRequest;
import com.musicshop.dto.checkout.CheckoutResponse;
import com.musicshop.exception.CartEmptyException;
import com.musicshop.exception.PaymentFailedException;
import com.musicshop.exception.ResourceNotFoundException;
import com.musicshop.model.address.Address;
import com.musicshop.model.cart.Cart;
import com.musicshop.model.cart.CartDetail;
import com.musicshop.model.order.UserOrder;
import com.musicshop.model.order.UserOrderBuilder;
import com.musicshop.model.payment.Payment;
import com.musicshop.model.user.Notification;
import com.musicshop.model.user.NotificationType;
import com.musicshop.model.user.User;
import com.musicshop.payment.PaymentGateway;
import com.musicshop.payment.PaymentGatewayFactory;
import com.musicshop.payment.PaymentRequest;
import com.musicshop.payment.PaymentResult;
import com.musicshop.pricing.BasePriceCalculator;
import com.musicshop.pricing.PriceCalculator;
import com.musicshop.pricing.ShippingFeeDecorator;
import com.musicshop.pricing.TaxDecorator;
import com.musicshop.repository.address.AddressRepository;
import com.musicshop.repository.cart.CartRepository;
import com.musicshop.repository.order.OrderRepository;
import com.musicshop.repository.payment.PaymentRepository;
import com.musicshop.repository.user.NotificationRepository;
import com.musicshop.service.cart.CartService;
import com.musicshop.service.notification.NotificationSseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckoutFacade {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.21");
    private static final BigDecimal SHIPPING_FEE = new BigDecimal("5.99");

    private final CartService cartService;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayFactory paymentGatewayFactory;
    private final NotificationRepository notificationRepository;
    private final NotificationSseService sseService;

    @Autowired
    public CheckoutFacade(CartService cartService,
                          CartRepository cartRepository,
                          AddressRepository addressRepository,
                          OrderRepository orderRepository,
                          PaymentRepository paymentRepository,
                          PaymentGatewayFactory paymentGatewayFactory,
                          NotificationRepository notificationRepository,
                          NotificationSseService sseService) {
        this.cartService = cartService;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.paymentGatewayFactory = paymentGatewayFactory;
        this.notificationRepository = notificationRepository;
        this.sseService = sseService;
    }

    @Transactional
    public CheckoutResponse checkout(User user, CheckoutRequest request) {
        // 1. Load cart
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        List<CartDetail> cartDetails = cartService.listCartDetails(cart.getId());
        if (cartDetails.isEmpty()) {
            throw new CartEmptyException("Cart is empty");
        }

        // 2. Create shipping address
        Address address = new Address();
        address.setStreet(request.getStreet());
        address.setNumber(request.getNumber());
        address.setPostalCode(request.getPostalCode());
        address.setCity(request.getCity());
        address.setCountry(request.getCountry());
        address = addressRepository.save(address);

        // 3. Calculate subtotal from cart items
        BigDecimal subtotal = cartDetails.stream()
                .map(cd -> cd.getProduct().getPrice().multiply(BigDecimal.valueOf(cd.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Apply Decorator pricing pipeline
        PriceCalculator calculator = new BasePriceCalculator(subtotal);
        calculator = new TaxDecorator(calculator, TAX_RATE);
        calculator = new ShippingFeeDecorator(calculator, SHIPPING_FEE);
        BigDecimal totalAmount = calculator.calculate();

        // 5. Build order using Builder pattern
        UserOrderBuilder builder = UserOrder.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .shippingAddress(address)
                .totalAmount(totalAmount);

        for (CartDetail cd : cartDetails) {
            builder.addOrderDetail(cd.getProduct(), cd.getQuantity(), cd.getProduct().getPrice());
        }

        UserOrder order = builder.build();

        // 6. Process payment using Adapter pattern
        PaymentGateway gateway = paymentGatewayFactory.getGateway(request.getPaymentMethod());
        PaymentRequest paymentRequest = new PaymentRequest(totalAmount, "EUR", request.getPaymentMethod());
        PaymentResult paymentResult = gateway.processPayment(paymentRequest);

        if (!paymentResult.isSuccess()) {
            throw new PaymentFailedException("Payment failed: " + paymentResult.getMessage());
        }

        // 7. Save order
        order.setStatus("CONFIRMED");
        UserOrder savedOrder = orderRepository.save(order);

        // 8. Save payment record
        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setAmount(totalAmount);
        paymentRepository.save(payment);

        // 9. Clear the cart
        cartService.clearCart(cart.getId());

        // 10. Send notification
        sendOrderConfirmation(user, savedOrder);

        // 11. Return response
        return new CheckoutResponse(
                savedOrder.getId(),
                totalAmount,
                "CONFIRMED",
                paymentResult.getTransactionId()
        );
    }

    private void sendOrderConfirmation(User user, UserOrder order) {
        Notification notification = new Notification();
        notification.setTimestamp(LocalDateTime.now());
        notification.setMessage(String.format("Order #%d confirmed! Total: EUR %.2f", order.getId(), order.getTotalAmount()));
        notification.setType(NotificationType.ORDER_CONFIRMED);
        notification.setUser(user);
        notification.setRelatedEntityId(order.getId());
        notification.setRead(false);

        Notification saved = notificationRepository.save(notification);
        sseService.sendToUser(user.getId(), saved);
    }
}
