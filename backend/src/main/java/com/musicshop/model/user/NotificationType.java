package com.musicshop.model.user;

public enum NotificationType {
    PRODUCT_UPDATE, // Product you're watching was updated
    PRICE_DROP, // Product price decreased
    BACK_IN_STOCK, // Out-of-stock product available again
    ORDER_CONFIRMED, // Your order was confirmed
    ORDER_SHIPPED, // Your order was shipped
    CART_REMINDER, // Items left in cart
    WISHLIST_SALE // Wishlist item on sale
}
