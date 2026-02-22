package com.musicshop.service.checkout;

import com.musicshop.dto.checkout.CheckoutRequest;
import com.musicshop.dto.checkout.CheckoutResponse;
import com.musicshop.exception.CartEmptyException;
import com.musicshop.exception.ResourceNotFoundException;
import com.musicshop.mapper.CheckoutMapper;
import com.musicshop.model.address.Address;
import com.musicshop.model.cart.Cart;
import com.musicshop.model.cart.CartDetail;
import com.musicshop.model.order.UserOrder;
import com.musicshop.model.user.User;
import com.musicshop.service.payment.PaymentResult;
import com.musicshop.repository.cart.CartRepository;
import com.musicshop.repository.user.UserRepository;
import com.musicshop.service.cart.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CheckoutFacade {

    private static final BigDecimal DEFAULT_COUPON_AMOUNT = new BigDecimal("10.00");

    private final CartService cartService;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CheckoutAddressService checkoutAddressService;
    private final CheckoutPricingService checkoutPricingService;
    private final CheckoutPaymentService checkoutPaymentService;
    private final CheckoutOrderService checkoutOrderService;
    private final CheckoutNotificationService checkoutNotificationService;
    private final CheckoutMapper checkoutMapper;
    private final BigDecimal couponAmount;

    @Autowired
    public CheckoutFacade(CartService cartService,
            CartRepository cartRepository,
            UserRepository userRepository,
            CheckoutAddressService checkoutAddressService,
            CheckoutPricingService checkoutPricingService,
            CheckoutPaymentService checkoutPaymentService,
            CheckoutOrderService checkoutOrderService,
            CheckoutNotificationService checkoutNotificationService,
            CheckoutMapper checkoutMapper,
            @Value("${checkout.coupon.fixed-amount:10.00}") BigDecimal couponAmount) {
        this.cartService = cartService;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.checkoutAddressService = checkoutAddressService;
        this.checkoutPricingService = checkoutPricingService;
        this.checkoutPaymentService = checkoutPaymentService;
        this.checkoutOrderService = checkoutOrderService;
        this.checkoutNotificationService = checkoutNotificationService;
        this.checkoutMapper = checkoutMapper;
        this.couponAmount = couponAmount != null ? couponAmount : DEFAULT_COUPON_AMOUNT;
    }

    @Transactional
    public CheckoutResponse checkout(Long userId, CheckoutRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 1. Load cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        List<CartDetail> cartDetails = cartService.listCartDetails(cart.getId());
        if (cartDetails.isEmpty()) {
            throw new CartEmptyException("Cart is empty");
        }

        // 2. Create shipping address
        Address address = checkoutAddressService.createShippingAddress(request);

        // 3. Calculate total using pricing pipeline
        BigDecimal totalAmount = checkoutPricingService.calculateTotal(cartDetails, request.getCouponCode(),
                couponAmount);

        // 4. Build order
        UserOrder order = checkoutOrderService.buildOrder(user, address, cartDetails, totalAmount);

        // 5. Process payment
        PaymentResult paymentResult = checkoutPaymentService.processPayment(request.getPaymentMethod(), totalAmount);

        // 6. Persist order and payment
        UserOrder savedOrder = checkoutOrderService.saveConfirmedOrder(order);
        checkoutOrderService.recordPayment(savedOrder, request.getPaymentMethod(), totalAmount);

        // 7. Clear the cart
        cartService.clearCart(cart.getId());

        // 8. Send notification
        checkoutNotificationService.sendOrderConfirmation(user, savedOrder);

        // 9. Return response
        return checkoutMapper.toCheckoutResponse(
                savedOrder.getId(),
                totalAmount,
                "CONFIRMED",
                paymentResult.getTransactionId());
    }

}
