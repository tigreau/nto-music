package com.musicshop.service.product;

import com.musicshop.discount.DiscountStrategy;
import com.musicshop.discount.DiscountStrategyFactory;
import com.musicshop.dto.product.ProductDTOFactory;
import com.musicshop.dto.product.SimpleProductDTO;
import com.musicshop.event.product.ProductDeletionEvent;
import com.musicshop.event.product.ProductDiscountEvent;
import com.musicshop.model.cart.CartDetail;
import com.musicshop.model.product.Product;
import com.musicshop.model.product.ProductCondition;
import com.musicshop.event.product.ProductUpdateEvent;
import com.musicshop.repository.cart.CartDetailRepository;
import com.musicshop.repository.category.CategoryRepository;
import com.musicshop.repository.product.ProductRepository;
import com.musicshop.specification.ProductSpecification;
import com.musicshop.validation.product.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;

import javax.xml.bind.ValidationException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final DiscountStrategyFactory discountStrategyFactory;
    private final CartDetailRepository cartDetailRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
            ApplicationEventPublisher eventPublisher,
            DiscountStrategyFactory discountStrategyFactory,
            CartDetailRepository cartDetailRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.eventPublisher = eventPublisher;
        this.discountStrategyFactory = discountStrategyFactory;
        this.cartDetailRepository = cartDetailRepository;
    }

    /**
     * List products with filtering, sorting, and pagination.
     */
    public Page<SimpleProductDTO> listProducts(String categorySlug, List<String> brandSlugs,
            BigDecimal minPrice, BigDecimal maxPrice,
            List<ProductCondition> conditions,
            String sort, int page, int size) {

        Specification<Product> spec = Specification.where(ProductSpecification.hasCategory(categorySlug))
                .and(ProductSpecification.hasBrands(brandSlugs))
                .and(ProductSpecification.hasMinPrice(minPrice))
                .and(ProductSpecification.hasMaxPrice(maxPrice))
                .and(ProductSpecification.hasConditions(conditions));

        Sort sorting = resolveSort(sort);
        Pageable pageable = PageRequest.of(page, size, sorting);

        return productRepository.findAll(spec, pageable)
                .map(ProductDTOFactory::createSimpleProductDTO);
    }

    private Sort resolveSort(String sort) {
        if (sort == null)
            sort = "recommended";
        switch (sort) {
            case "price_asc":
                return Sort.by(Sort.Direction.ASC, "price");
            case "price_desc":
                return Sort.by(Sort.Direction.DESC, "price");
            case "newest":
                return Sort.by(Sort.Direction.DESC, "createdAt");
            case "recommended":
            default:
                return Sort.by(
                        Sort.Order.desc("isPromoted"),
                        Sort.Order.desc("createdAt"));
        }
    }

    public List<Product> listAllProducts() {
        return productRepository.findAll();
    }

    public Product createProduct(Product product) throws ValidationException {
        ProductValidator validator = new CategoryValidationDecorator(
                new QuantityAvailableValidationDecorator(
                        new DescriptionValidationDecorator(
                                new PriceValidationDecorator(
                                        new NameValidationDecorator(
                                                new BasicProductValidator())))));
        validator.validate(product);

        // Handle category logic
        product.setCategory(categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found")));

        return productRepository.save(product);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id).map(product -> {
            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setQuantityAvailable(productDetails.getQuantityAvailable());
            product.setCategory(productDetails.getCategory());
            product.setCondition(productDetails.getCondition());

            Product updatedProduct = productRepository.save(product);
            eventPublisher.publishEvent(new ProductUpdateEvent(this, updatedProduct));
            return updatedProduct;
        });
    }

    public Optional<Product> partialUpdateProduct(Long id, Map<String, Object> updates) {
        return productRepository.findById(id).map(product -> {
            applyPartialUpdates(product, updates);

            Product updatedProduct = productRepository.save(product);
            eventPublisher.publishEvent(new ProductUpdateEvent(this, updatedProduct));
            return updatedProduct;
        });
    }

    private void applyPartialUpdates(Product product, Map<String, Object> updates) {
        if (updates.containsKey("name")) {
            product.setName((String) updates.get("name"));
        }
        if (updates.containsKey("price")) {
            product.setPrice(new BigDecimal(String.valueOf(updates.get("price"))));
        }
    }

    public void deleteProduct(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            List<CartDetail> cartDetails = cartDetailRepository.findByProductId(id);
            cartDetailRepository.deleteAll(cartDetails);
            productRepository.delete(product);
            eventPublisher.publishEvent(new ProductDeletionEvent(this, product, cartDetails));
        } else {
            throw new RuntimeException("Product not found");
        }
    }

    public Optional<Product> applyDiscount(Long productId, String discountType) {
        return productRepository.findById(productId).map(product -> {
            BigDecimal originalPrice = product.getPrice();
            DiscountStrategy discountStrategy = discountStrategyFactory.getDiscountStrategy(discountType);
            BigDecimal discountedPrice = discountStrategy.applyDiscount(product);
            product.setPrice(discountedPrice);
            Product savedProduct = productRepository.save(product);
            eventPublisher.publishEvent(new ProductDiscountEvent(savedProduct, originalPrice));
            return savedProduct;
        });
    }
}
