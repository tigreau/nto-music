import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Shield, Plus, Save, Trash2, Tag } from 'lucide-react';

interface Category {
    id: number;
    name: string;
    productCount: number;
}

interface Product {
    id: number;
    name: string;
    price: number;
    description?: string;
    quantityAvailable?: number;
    category?: { id: number; name?: string };
}

const AdminPage = () => {
    const [products, setProducts] = useState<Product[]>([]);
    const [categories, setCategories] = useState<Category[]>([]);
    const [newProduct, setNewProduct] = useState({
        name: '',
        price: 0,
        description: '',
        quantityAvailable: 0,
        category: { id: 0 }
    });
    const [selectedDiscount, setSelectedDiscount] = useState('fixed');

    useEffect(() => {
        fetchProducts();
        fetchCategories();
    }, []);

    const fetchCategories = () => {
        fetch('/api/categories')
            .then(res => res.json())
            .then((data: Category[]) => {
                setCategories(data);
                if (data.length > 0 && (newProduct.category.id === 0 || newProduct.category.id === 1)) {
                    setNewProduct(prev => ({ ...prev, category: { id: data[0].id } }));
                }
            })
            .catch(err => console.error('Error fetching categories:', err));
    };

    const handleCreateCategory = () => {
        const name = prompt("Enter new category name:");
        if (name) {
            fetch('/api/categories', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ categoryName: name })
            })
                .then(res => {
                    if (res.ok) return res.json();
                    throw new Error('Failed to create category');
                })
                .then(() => {
                    fetchCategories(); // Refresh list
                    alert('Category created!');
                })
                .catch(err => alert(err.message));
        }
    };

    const fetchProducts = () => {
        fetch('/api/products')
            .then(response => response.json())
            .then(data => setProducts(data))
            .catch(error => console.error('Error fetching products:', error));
    };

    const addProduct = () => {
        fetch('/api/products', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(newProduct)
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    return response.text().then(text => {
                        throw new Error(text);
                    });
                }
            })
            .then(() => {
                fetchProducts();
                setNewProduct({
                    name: '',
                    price: 0,
                    description: '',
                    quantityAvailable: 0,
                    category: { id: categories.length > 0 ? categories[0].id : 0 }
                });
            })
            .catch(error => {
                alert(`Error adding product: ${error.message}`);
                console.error('Error adding product:', error);
            });
    };

    const handleEdit = (id: number, updatedProduct: Partial<Product>) => {
        fetch(`/api/products/${id}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(updatedProduct)
        })
            .then(response => {
                if (response.ok) {
                    fetchProducts();
                } else {
                    console.error('Failed to update product');
                }
            })
            .catch(error => console.error('Error updating product:', error));
    };

    const deleteProduct = (id: number) => {
        if (window.confirm('Are you sure you want to delete this product?')) {
            fetch(`/api/products/${id}`, { method: 'DELETE' })
                .then(response => {
                    if (response.ok) {
                        fetchProducts();
                    } else {
                        console.error('Failed to delete product');
                    }
                })
                .catch(error => console.error('Error deleting product:', error));
        }
    };

    const applyDiscount = (id: number, discountType: string) => {
        fetch(`/api/products/${id}/apply-discount?discountType=${discountType}`, {
            method: 'PATCH'
        })
            .then(response => {
                if (response.ok) {
                    fetchProducts();
                } else {
                    console.error('Failed to apply discount');
                }
            })
            .catch(error => console.error('Error applying discount:', error));
    };

    const handleNameChange = (id: number, newName: string) => {
        setProducts(products.map(product =>
            product.id === id ? { ...product, name: newName } : product
        ));
    };

    const handlePriceChange = (id: number, newPrice: number) => {
        setProducts(products.map(product =>
            product.id === id ? { ...product, price: newPrice } : product
        ));
    };

    return (
        <div className="container mx-auto px-4 py-8">
            <div className="max-w-5xl mx-auto">
                <div className="flex items-center gap-3 mb-8">
                    <Shield className="w-8 h-8 text-primary" />
                    <h1 className="font-[family-name:var(--font-display)] text-3xl text-foreground">
                        Admin Dashboard
                    </h1>
                </div>

                {/* Add Product Form */}
                <div className="bg-card rounded-xl border border-border p-6 mb-8">
                    <h2 className="font-semibold text-lg text-foreground mb-4 flex items-center gap-2">
                        <Plus className="w-5 h-5" />
                        Add New Product
                    </h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
                        <Input
                            type="text"
                            placeholder="Product Name"
                            value={newProduct.name}
                            onChange={e => setNewProduct({ ...newProduct, name: e.target.value })}
                        />
                        <Input
                            type="number"
                            placeholder="Price"
                            value={newProduct.price || ''}
                            onChange={e => setNewProduct({ ...newProduct, price: parseFloat(e.target.value) || 0 })}
                        />
                        <Input
                            type="text"
                            placeholder="Description"
                            value={newProduct.description}
                            onChange={e => setNewProduct({ ...newProduct, description: e.target.value })}
                        />
                        <Input
                            type="number"
                            placeholder="Quantity"
                            value={newProduct.quantityAvailable || ''}
                            onChange={e => setNewProduct({ ...newProduct, quantityAvailable: parseInt(e.target.value) || 0 })}
                        />
                        <div className="flex gap-2">
                            <select
                                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                                value={newProduct.category.id}
                                onChange={e => setNewProduct({ ...newProduct, category: { id: parseInt(e.target.value) } })}
                            >
                                {categories.map(cat => (
                                    <option key={cat.id} value={cat.id}>{cat.name}</option>
                                ))}
                            </select>
                            <Button variant="outline" onClick={handleCreateCategory} type="button">
                                <Plus className="w-4 h-4" />
                            </Button>
                        </div>
                    </div>
                    <Button onClick={addProduct}>
                        <Plus className="w-4 h-4 mr-2" />
                        Add Product
                    </Button>
                </div>

                {/* Product List */}
                <div className="bg-card rounded-xl border border-border overflow-hidden">
                    <div className="p-4 border-b border-border">
                        <h2 className="font-semibold text-lg text-foreground">
                            Product List ({products.length} products)
                        </h2>
                    </div>
                    <div className="divide-y divide-border">
                        {products.map(product => (
                            <div key={product.id} className="p-4">
                                <div className="flex flex-wrap items-center gap-3">
                                    <div className="flex-1 min-w-0 grid grid-cols-1 sm:grid-cols-2 gap-3">
                                        <Input
                                            type="text"
                                            value={product.name}
                                            onChange={(e) => handleNameChange(product.id, e.target.value)}
                                            className="font-medium"
                                        />
                                        <Input
                                            type="number"
                                            value={product.price}
                                            onChange={(e) => handlePriceChange(product.id, parseFloat(e.target.value))}
                                        />
                                    </div>
                                    <div className="flex items-center gap-2 flex-wrap">
                                        <Button
                                            size="sm"
                                            onClick={() => handleEdit(product.id, { name: product.name, price: product.price })}
                                        >
                                            <Save className="w-4 h-4 mr-1" />
                                            Save
                                        </Button>
                                        <Button
                                            size="sm"
                                            variant="destructive"
                                            onClick={() => deleteProduct(product.id)}
                                        >
                                            <Trash2 className="w-4 h-4 mr-1" />
                                            Delete
                                        </Button>
                                        <div className="flex items-center gap-2">
                                            <select
                                                className="h-8 rounded-md border border-input bg-background px-2 text-sm"
                                                value={selectedDiscount}
                                                onChange={(e) => setSelectedDiscount(e.target.value)}
                                            >
                                                <option value="fixed">Fixed Amount</option>
                                                <option value="percentage">Percentage</option>
                                            </select>
                                            <Button
                                                size="sm"
                                                variant="outline"
                                                onClick={() => applyDiscount(product.id, selectedDiscount)}
                                            >
                                                <Tag className="w-4 h-4 mr-1" />
                                                Discount
                                            </Button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AdminPage;
