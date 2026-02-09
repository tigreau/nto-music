import Product from './Product';

interface ProductData {
    id: number;
    name: string;
    price: number;
    quantityAvailable?: number;
}

interface ProductListProps {
    products: ProductData[];
    onProductClick: (product: ProductData) => void;
    isAdmin: boolean;
}

const ProductList = ({ products, onProductClick, isAdmin }: ProductListProps) => {
    return (
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            {products.map(product => (
                <Product 
                    key={product.id} 
                    product={product} 
                    onProductClick={onProductClick} 
                    isAdmin={isAdmin} 
                />
            ))}
        </div>
    );
};

export default ProductList;
