import { Review } from '@/types';
import './ReviewSection.css';

interface ReviewSectionProps {
    categoryName: string;
    averageRating: number | null;
    reviewCount: number;
    reviews: Review[];
    isLoading?: boolean;
}

function StarRating({ rating }: { rating: number }) {
    return (
        <span className="star-rating">
            {[1, 2, 3, 4, 5].map(star => (
                <span key={star} className={star <= rating ? 'star filled' : 'star'}>★</span>
            ))}
        </span>
    );
}

export function ReviewSection({ categoryName, averageRating, reviewCount, reviews, isLoading }: ReviewSectionProps) {
    if (isLoading) {
        return <div className="review-section"><p className="review-loading">Loading reviews...</p></div>;
    }

    return (
        <section className="review-section">
            <div className="review-header">
                <h2 className="review-title">
                    {categoryName} Reviews
                </h2>
                {averageRating !== null && (
                    <div className="review-summary">
                        <StarRating rating={Math.round(averageRating)} />
                        <span className="review-avg">{averageRating.toFixed(1)}</span>
                        <span className="review-count">({reviewCount} review{reviewCount !== 1 ? 's' : ''})</span>
                    </div>
                )}
            </div>

            {reviews.length === 0 ? (
                <p className="review-empty">No reviews yet for this category.</p>
            ) : (
                <div className="review-list">
                    {reviews.map(review => (
                        <div key={review.id} className="review-card">
                            <div className="review-card-header">
                                <div className="review-user-info">
                                    <span className="review-user">{review.userName}</span>
                                    {review.verifiedPurchase && (
                                        <span className="verified-badge">✓ Verified Purchase</span>
                                    )}
                                </div>
                                <StarRating rating={review.rating} />
                            </div>
                            <p className="review-comment">{review.comment}</p>
                            <div className="review-product">
                                {review.productThumbnailUrl && (
                                    <img
                                        src={review.productThumbnailUrl}
                                        alt={review.productName}
                                        className="review-product-thumb"
                                    />
                                )}
                                <span className="review-product-name">
                                    Purchased: {review.productName}
                                </span>
                            </div>
                            <span className="review-date">
                                {new Date(review.datePosted).toLocaleDateString('en-US', {
                                    year: 'numeric', month: 'short', day: 'numeric'
                                })}
                            </span>
                        </div>
                    ))}
                </div>
            )}
        </section>
    );
}
