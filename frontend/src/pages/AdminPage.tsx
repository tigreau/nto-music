import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Shield, Plus, Save, Trash2, Tag, FolderPlus } from 'lucide-react';
import { getToken } from '@/api/client';

interface Category {
    id: number;
    name: string;
    productCount: number;
    subCategories?: Category[];
}

interface Product {
    id: number;
    name: string;
    price: number;
    description?: string;
    condition?: string;
    quantityAvailable?: number;
    category?: { id: number; name?: string };
}

const AdminPage = () => {
    const [products, setProducts] = useState<Product[]>([]);

    // Category Data
    const [allCategories, setAllCategories] = useState<Category[]>([]); // Full hierarchy
    const [parentCategories, setParentCategories] = useState<Category[]>([]); // Just parents

    // Selection state for "Manage Categories"
    const [selectedParentId, setSelectedParentId] = useState<number>(0);
    const [newSubcategoryName, setNewSubcategoryName] = useState('');

    // Form for new product
    const [newProduct, setNewProduct] = useState({
        name: '',
        price: 0,
        description: '',
        quantityAvailable: 0,
        condition: 'NEW',
        category: { id: 0 }
    });
    // Product creation category selection state
    const [productParentId, setProductParentId] = useState<number>(0);

    const [selectedDiscount, setSelectedDiscount] = useState('fixed');

    useEffect(() => {
        fetchProducts();
        fetchCategories();
    }, []);

    const fetchCategories = () => {
        fetch('/api/categories')
            .then(res => res.json())
            .then((data: Category[]) => {
                setAllCategories(data);
                setParentCategories(data);

                // Initialize selection if data exists
                if (data.length > 0) {
                    if (selectedParentId === 0) setSelectedParentId(data[0].id);
                    if (productParentId === 0) setProductParentId(data[0].id);
                }
            })
            .catch(err => console.error('Error fetching categories:', err));
    };

    const handleCreateSubcategory = () => {
        if (!newSubcategoryName || !selectedParentId) {
            alert("Please provide a name and select a parent category.");
            return;
        }

        const token = getToken();
        fetch(`/api/categories?parentId=${selectedParentId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...(token ? { 'Authorization': `Bearer ${token}` } : {})
            },
            body: JSON.stringify({
                categoryName: newSubcategoryName
            })
        })
            .then(res => {
                if (res.ok) return res.json();
                throw new Error('Failed to create subcategory');
            })
            .then(() => {
                fetchCategories(); // Refresh list
                setNewSubcategoryName('');
                alert('Subcategory created!');
            })
            .catch(err => alert(err.message));
    };

    const fetchProducts = () => {
        fetch('/api/products')
            .then(response => response.json())
            .then(data => {
                const items = Array.isArray(data) ? data : (data.content || []);
                setProducts(items);
            })
            .catch(error => console.error('Error fetching products:', error));
    };

    const addProduct = () => {
        if (newProduct.category.id === 0) {
            alert("Please select a valid subcategory.");
            return;
        }

        const token = getToken();
        fetch('/api/products', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...(token ? { 'Authorization': `Bearer ${token}` } : {})
            },
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
                    condition: 'NEW',
                    category: { id: 0 }
                });
                alert("Product added successfully!");
            })
            .catch(error => {
                alert(`Error adding product: ${error.message}`);
                console.error('Error adding product:', error);
            });
    };

    const handleEdit = (id: number, updatedProduct: Partial<Product>) => {
        const token = getToken();
        fetch(`/api/products/${id}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                ...(token ? { 'Authorization': `Bearer ${token}` } : {})
            },
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
            const token = getToken();
            fetch(`/api/products/${id}`, {
                method: 'DELETE',
                headers: token ? { 'Authorization': `Bearer ${token}` } : {}
            })
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
        const token = getToken();
        fetch(`/api/products/${id}/apply-discount?discountType=${discountType}`, {
            method: 'PATCH',
            headers: token ? { 'Authorization': `Bearer ${token}` } : {}
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

    // Helpers to get subcategories for selected parent
    const getSubcategories = (parentId: number) => {
        const parent = allCategories.find(p => p.id === parentId);
        return parent?.subCategories || [];
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

                {/* Manage Categories Section */}
                <div className="bg-card rounded-xl border border-border p-6 mb-8">
                    <div className="flex items-center gap-2 mb-6">
                        <FolderPlus className="w-6 h-6 text-foreground" />
                        <h2 className="font-semibold text-xl text-foreground">Manage Categories</h2>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-12 gap-6">
                        {/* Left Column: Parent Category Selection (The "Unchangeable Dropdown") */}
                        <div className="md:col-span-4 space-y-2">
                            <label className="text-sm font-medium text-muted-foreground">Select Parent Category</label>
                            <select
                                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                                value={selectedParentId}
                                onChange={e => setSelectedParentId(parseInt(e.target.value))}
                            >
                                {parentCategories.map(cat => (
                                    <option key={cat.id} value={cat.id}>{cat.name}</option>
                                ))}
                            </select>
                            <p className="text-xs text-muted-foreground">
                                Select a parent category to view or add subcategories.
                            </p>
                        </div>

                        {/* Right Column: Subcategories and Add New */}
                        <div className="md:col-span-8 space-y-4">
                            {/* Existing Subcategories List (Visual only) */}
                            <div className="bg-muted/30 rounded-md p-3 border border-border">
                                <label className="text-xs font-semibold text-muted-foreground uppercase mb-2 block">
                                    Existing Subcategories
                                </label>
                                <div className="flex flex-wrap gap-2">
                                    {getSubcategories(selectedParentId).length > 0 ? (
                                        getSubcategories(selectedParentId).map(sub => (
                                            <span key={sub.id} className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-primary/10 text-primary">
                                                {sub.name}
                                            </span>
                                        ))
                                    ) : (
                                        <span className="text-sm text-muted-foreground italic">No subcategories yet.</span>
                                    )}
                                </div>
                            </div>

                            {/* Add New Subcategory Form */}
                            <div className="flex gap-2 items-end">
                                <div className="flex-1 space-y-2">
                                    <label className="text-sm font-medium text-foreground">Add New Subcategory</label>
                                    <Input
                                        placeholder={`New ${parentCategories.find(p => p.id === selectedParentId)?.name || 'Subcategory'} Type...`}
                                        value={newSubcategoryName}
                                        onChange={e => setNewSubcategoryName(e.target.value)}
                                        className="bg-background"
                                    />
                                </div>
                                <Button onClick={handleCreateSubcategory}>
                                    <Plus className="w-4 h-4 mr-2" />
                                    Add
                                </Button>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Add Product Form */}
                <div className="bg-card rounded-xl border border-border p-6 mb-8">
                    <h2 className="font-semibold text-lg text-foreground mb-4 flex items-center gap-2">
                        <Plus className="w-5 h-5" />
                        Add New Product
                    </h2>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                        {/* Product Details */}
                        <div className="space-y-4">
                            <Input
                                type="text"
                                placeholder="Product Name"
                                value={newProduct.name}
                                onChange={e => setNewProduct({ ...newProduct, name: e.target.value })}
                            />
                            <div className="grid grid-cols-2 gap-4">
                                <Input
                                    type="number"
                                    placeholder="Price"
                                    value={newProduct.price || ''}
                                    onChange={e => setNewProduct({ ...newProduct, price: parseFloat(e.target.value) || 0 })}
                                />
                                <Input
                                    type="number"
                                    placeholder="Quantity"
                                    value={newProduct.quantityAvailable || ''}
                                    onChange={e => setNewProduct({ ...newProduct, quantityAvailable: parseInt(e.target.value) || 0 })}
                                />
                            </div>
                            <div className="space-y-1">
                                <label className="text-[10px] font-semibold text-muted-foreground uppercase px-1">Condition</label>
                                <select
                                    className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                                    value={newProduct.condition}
                                    onChange={e => setNewProduct({ ...newProduct, condition: e.target.value })}
                                >
                                    <option value="NEW">New</option>
                                    <option value="EXCELLENT">Excellent</option>
                                    <option value="VERY_GOOD">Very Good</option>
                                    <option value="GOOD">Good</option>
                                    <option value="FAIR">Fair</option>
                                </select>
                            </div>
                            <Input
                                type="text"
                                placeholder="Description"
                                value={newProduct.description}
                                onChange={e => setNewProduct({ ...newProduct, description: e.target.value })}
                            />
                        </div>

                        {/* Category Selection */}
                        <div className="bg-muted/30 p-4 rounded-lg border border-border space-y-4">
                            <h3 className="font-medium text-sm">Product Category</h3>

                            <div className="space-y-2">
                                <label className="text-xs text-muted-foreground">1. Select Parent Category</label>
                                <select
                                    className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                                    value={productParentId}
                                    onChange={e => {
                                        setProductParentId(parseInt(e.target.value));
                                        setNewProduct(prev => ({ ...prev, category: { id: 0 } })); // Reset subcategory
                                    }}
                                >
                                    {parentCategories.map(cat => (
                                        <option key={cat.id} value={cat.id}>{cat.name}</option>
                                    ))}
                                </select>
                            </div>

                            <div className="space-y-2">
                                <label className="text-xs text-muted-foreground">2. Select Subcategory</label>
                                <select
                                    className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring disabled:opacity-50"
                                    value={newProduct.category.id}
                                    onChange={e => setNewProduct({ ...newProduct, category: { id: parseInt(e.target.value) } })}
                                    disabled={getSubcategories(productParentId).length === 0}
                                >
                                    <option value="0">-- Select Subcategory --</option>
                                    {getSubcategories(productParentId).map(cat => (
                                        <option key={cat.id} value={cat.id}>{cat.name}</option>
                                    ))}
                                </select>
                                {getSubcategories(productParentId).length === 0 && (
                                    <p className="text-xs text-destructive">No subcategories found. Please add one above first.</p>
                                )}
                            </div>

                            <Button onClick={addProduct} className="w-full mt-2" disabled={newProduct.category.id === 0}>
                                <Plus className="w-4 h-4 mr-2" />
                                Add Product
                            </Button>
                        </div>
                    </div>
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
