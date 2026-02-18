package com.musicshop.service.product;

import com.musicshop.discount.DiscountStrategy;
import com.musicshop.discount.DiscountStrategyFactory;
import com.musicshop.discount.DiscountType;
import com.musicshop.exception.ResourceNotFoundException;
import com.musicshop.exception.ResourceInUseException;
import com.musicshop.mapper.ProductMapper;
import com.musicshop.dto.product.ProductUpsertRequest;
import com.musicshop.dto.product.ProductPatchRequest;
import com.musicshop.dto.product.SimpleProductDTO;
import com.musicshop.dto.product.DetailedProductDTO;
import com.musicshop.model.product.ProductSortType;
import com.musicshop.event.product.ProductDeletionEvent;
import com.musicshop.event.product.ProductDiscountEvent;
import com.musicshop.model.cart.CartDetail;
import com.musicshop.model.product.Product;
import com.musicshop.model.product.ProductCondition;
import com.musicshop.event.product.ProductUpdateEvent;
import com.musicshop.repository.cart.CartDetailRepository;
import com.musicshop.repository.category.CategoryRepository;
import com.musicshop.repository.order.OrderDetailRepository;
import com.musicshop.repository.product.ProductRepository;
import com.musicshop.repository.review.ReviewRepository;
import com.musicshop.specification.ProductSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final DiscountStrategyFactory discountStrategyFactory;
    private final CartDetailRepository cartDetailRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ReviewRepository reviewRepository;
    private final ProductMapper productMapper;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
            ApplicationEventPublisher eventPublisher,
            DiscountStrategyFactory discountStrategyFactory,
            CartDetailRepository cartDetailRepository,
            OrderDetailRepository orderDetailRepository,
            ReviewRepository reviewRepository,
            ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.eventPublisher = eventPublisher;
        this.discountStrategyFactory = discountStrategyFactory;
        this.cartDetailRepository = cartDetailRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.reviewRepository = reviewRepository;
        this.productMapper = productMapper;
    }

    /**
     * List products with filtering, sorting, and pagination.
     */

    // ... existing imports

    public Page<SimpleProductDTO> listProducts(String categorySlug, List<String> brandSlugs,
            BigDecimal minPrice, BigDecimal maxPrice,
            String condition,
            String sort, int page, int size) {
        List<ProductCondition> conditions = condition != null
                ? Arrays.stream(condition.split(","))
                        .map(ProductCondition::valueOf)
                        .collect(Collectors.toList())
                : Collections.emptyList();

        Specification<Product> spec = Specification.where(ProductSpecification.hasCategory(categorySlug))
                .and(ProductSpecification.hasBrands(brandSlugs))
                .and(ProductSpecification.hasMinPrice(minPrice))
                .and(ProductSpecification.hasMaxPrice(maxPrice))
                .and(ProductSpecification.hasConditions(conditions));

        ProductSortType sortType = ProductSortType.fromValue(sort).orElse(ProductSortType.RECOMMENDED);
        Sort sorting = resolveSort(sortType);
        Pageable pageable = PageRequest.of(page, size, sorting);

        return productRepository.findAll(spec, pageable)
                .map(productMapper::toSimpleProductDTO);
    }

    private Sort resolveSort(ProductSortType sortType) {
        switch (sortType) {
            case PRICE_ASC:
                return Sort.by(Sort.Direction.ASC, "price");
            case PRICE_DESC:
                return Sort.by(Sort.Direction.DESC, "price");
            case NEWEST:
                return Sort.by(Sort.Direction.DESC, "createdAt");
            case RECOMMENDED:
            default:
                return Sort.by(
                        Sort.Order.desc("isPromoted"),
                        Sort.Order.desc("createdAt"));
        }
    }

    public List<Product> listAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public DetailedProductDTO createProduct(ProductUpsertRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantityAvailable(request.getQuantityAvailable());
        product.setCondition(request.getCondition());
        product.setConditionNotes(request.getConditionNotes());

        // Handle category logic
        product.setCategory(categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found")));

        Product savedProduct = productRepository.save(product);
        return productMapper.toDetailedProductDTO(savedProduct);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    public Optional<DetailedProductDTO> getDetailedProductById(Long id) {
        return productRepository.findById(id).map(productMapper::toDetailedProductDTO);
    }

    @Transactional
    public Optional<DetailedProductDTO> updateProduct(Long id, ProductUpsertRequest request) {
        return productRepository.findById(id).map(product -> {
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setQuantityAvailable(request.getQuantityAvailable());
            product.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found")));
            product.setCondition(request.getCondition());
            product.setConditionNotes(request.getConditionNotes());

            Product updatedProduct = productRepository.save(product);
            eventPublisher.publishEvent(new ProductUpdateEvent(this, updatedProduct));
            return productMapper.toDetailedProductDTO(updatedProduct);
        });
    }

    @Transactional
    public Optional<DetailedProductDTO> partialUpdateProduct(Long id, ProductPatchRequest request) {
        return productRepository.findById(id).map(product -> {
            applyPartialUpdates(product, request);

            Product updatedProduct = productRepository.save(product);
            eventPublisher.publishEvent(new ProductUpdateEvent(this, updatedProduct));
            return productMapper.toDetailedProductDTO(updatedProduct);
        });
    }

    private void applyPartialUpdates(Product product, ProductPatchRequest request) {
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getQuantityAvailable() != null) {
            product.setQuantityAvailable(request.getQuantityAvailable());
        }
        if (request.getCategoryId() != null) {
            product.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found")));
        }
        if (request.getCondition() != null) {
            product.setCondition(request.getCondition());
        }
        if (request.getConditionNotes() != null) {
            product.setConditionNotes(request.getConditionNotes());
        }
    }

    @Transactional
    public void deleteProduct(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            if (orderDetailRepository.existsByProductId(id)) {
                throw new ResourceInUseException("Cannot delete product with existing order history.");
            }
            if (reviewRepository.existsByProductId(id)) {
                throw new ResourceInUseException("Cannot delete product with existing reviews.");
            }
            Product product = productOpt.get();
            List<CartDetail> cartDetails = cartDetailRepository.findByProductId(id);
            cartDetailRepository.deleteAll(cartDetails);
            productRepository.delete(product);
            eventPublisher.publishEvent(new ProductDeletionEvent(this, product, cartDetails));
        } else {
            throw new ResourceNotFoundException("Product not found");
        }
    }

    @Transactional
    public Optional<DetailedProductDTO> applyDiscount(Long productId, String discountType) {
        return productRepository.findById(productId).map(product -> {
            BigDecimal originalPrice = product.getPrice();
            // validate discount type
            DiscountType type = DiscountType.fromValue(discountType)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown discount type: " + discountType));
            DiscountStrategy discountStrategy = discountStrategyFactory.getDiscountStrategy(type);
            BigDecimal discountedPrice = discountStrategy.applyDiscount(product);
            product.setPrice(discountedPrice);
            Product savedProduct = productRepository.save(product);
            eventPublisher.publishEvent(new ProductDiscountEvent(savedProduct, originalPrice));
            return productMapper.toDetailedProductDTO(savedProduct);
        });
    }
}
