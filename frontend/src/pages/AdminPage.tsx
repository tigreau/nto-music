import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Shield, Plus, Save, Trash2, Tag, FolderPlus, Image as ImageIcon } from 'lucide-react';
import { ProductImagesModal } from '@/components/admin/ProductImagesModal';
import { toast } from 'sonner';

interface Category {
    id: number;
    name: string;
    productCount: number;
    subCategories?: Category[];
}

interface ProductImage {
    id: number;
    url: string;
    altText: string;
    isPrimary: boolean;
    displayOrder: number;
}

interface Product {
    id: number;
    name: string;
    price: number;
    description?: string;
    condition?: string;
    quantityAvailable?: number;
    category?: { id: number; name?: string };
    images?: ProductImage[];
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

    const [selectedDiscount, setSelectedDiscount] = useState('Fixed Amount');

    // Image Modal State
    const [imageModalOpen, setImageModalOpen] = useState(false);
    const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);

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
            toast.error('Please provide a name and select a parent category.');
            return;
        }

        fetch(`/api/categories?parentId=${selectedParentId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify({
                categoryName: newSubcategoryName
            })
        })
            .then(res => {
                if (res.ok) {
                    return res.json().then(() => {
                        fetchCategories();
                        setNewSubcategoryName('');
                        toast.success('Subcategory created');
                    });
                } else {
                    return res.json().then(data => {
                        toast.error(data.message || 'Failed to create subcategory');
                    });
                }
            })
            .catch(err => console.error('Error creating subcategory:', err));
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
            toast.error('Please select a valid subcategory.');
            return;
        }

        fetch('/api/products', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify(newProduct)
        })
            .then(response => {
                if (response.ok) {
                    return response.json().then(() => {
                        fetchProducts();
                        setNewProduct({
                            name: '',
                            price: 0,
                            description: '',
                            quantityAvailable: 0,
                            condition: 'NEW',
                            category: { id: 0 }
                        });
                        toast.success('Product added successfully');
                    });
                } else {
                    return response.json().then(data => {
                        toast.error(data.message || 'Failed to add product');
                    });
                }
            })
            .catch(error => console.error('Error adding product:', error));
    };

    const handleEdit = (id: number, updatedProduct: Partial<Product>) => {
        fetch(`/api/products/${id}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify(updatedProduct)
        })
            .then(response => {
                if (response.ok) {
                    fetchProducts();
                    toast.success('Product updated');
                } else {
                    return response.json().then(data => {
                        toast.error(data.message || 'Failed to update product');
                    });
                }
            })
            .catch(error => console.error('Error updating product:', error));
    };

    const deleteProduct = (id: number) => {
        fetch(`/api/products/${id}`, {
            method: 'DELETE',
            credentials: 'include'
        })
            .then(response => {
                if (response.ok) {
                    fetchProducts();
                    toast.success('Product deleted');
                } else {
                    toast.error('Failed to delete product');
                }
            })
            .catch(error => console.error('Error deleting product:', error));
    };

    const applyDiscount = (id: number, discountType: string) => {
        fetch(`/api/products/${id}/apply-discount?discountType=${discountType}`, {
            method: 'PATCH',
            credentials: 'include'
        })
            .then(response => {
                if (response.ok) {
                    fetchProducts();
                    toast.success('Discount applied');
                } else {
                    toast.error('Failed to apply discount');
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

    const openImageModal = (product: Product) => {
        setSelectedProduct(product);
        setImageModalOpen(true);
    };

    const handleImagesChange = (images: ProductImage[]) => {
        if (selectedProduct) {
            const updatedProducts = products.map(p =>
                p.id === selectedProduct.id ? { ...p, images } : p
            );
            setProducts(updatedProducts);
            setSelectedProduct({ ...selectedProduct, images });
        }
    };

    return (
        <div className="min-h-screen bg-background">
            {/* Header Section */}
            <div className="bg-[#073642] border-b-2 border-[#002b36]">
                <div className="container mx-auto px-4 py-6">
                    <div className="flex items-center gap-3">
                        <Shield className="w-8 h-8 text-[#268bd2]" />
                        <h1 className="font-[family-name:var(--font-display)] text-4xl text-[#fdf6e3] tracking-tight">
                            ADMIN DASHBOARD
                        </h1>
                    </div>
                </div>
            </div>

            <div className="container mx-auto px-4 py-8">
                <div className="max-w-6xl mx-auto space-y-6">
                    {/* Manage Categories Section */}
                    <div className="bg-[#eee8d5] rounded-lg border border-[#93a1a1] shadow-md">
                        <div className="bg-[#073642] px-6 py-4 rounded-t-lg border-b-2 border-[#002b36]">
                            <div className="flex items-center gap-3">
                                <FolderPlus className="w-6 h-6 text-[#2aa198]" />
                                <h2 className="font-bold text-xl text-[#fdf6e3]">Manage Categories</h2>
                            </div>
                        </div>

                        <div className="p-6">
                            <div className="grid grid-cols-1 md:grid-cols-12 gap-6">
                                {/* Left Column */}
                                <div className="md:col-span-4 space-y-3">
                                    <label className="text-sm font-bold text-[#073642] uppercase tracking-wide">
                                        Select Parent Category
                                    </label>
                                    <select
                                        className="flex h-11 w-full rounded-md border border-[#93a1a1] bg-[#fdf6e3] px-4 py-2 text-base font-medium text-[#073642] ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#268bd2] focus-visible:border-[#268bd2]"
                                        value={selectedParentId}
                                        onChange={e => setSelectedParentId(parseInt(e.target.value))}
                                    >
                                        {parentCategories.map(cat => (
                                            <option key={cat.id} value={cat.id}>{cat.name}</option>
                                        ))}
                                    </select>
                                    <p className="text-sm text-[#586e75] font-medium">
                                        Select a parent category to view or add subcategories.
                                    </p>
                                </div>

                                {/* Right Column */}
                                <div className="md:col-span-8 space-y-4">
                                    <div className="bg-[#fdf6e3] rounded-md p-4 border border-[#93a1a1]">
                                        <label className="text-xs font-bold text-[#586e75] uppercase mb-3 block tracking-wide">
                                            Existing Subcategories
                                        </label>
                                        <div className="flex flex-wrap gap-2">
                                            {getSubcategories(selectedParentId).length > 0 ? (
                                                getSubcategories(selectedParentId).map(sub => (
                                                    <span
                                                        key={sub.id}
                                                        className="inline-flex items-center px-3 py-1.5 rounded-full text-sm font-bold bg-[#268bd2] text-[#fdf6e3] border border-[#073642]"
                                                    >
                                                        {sub.name}
                                                    </span>
                                                ))
                                            ) : (
                                                <span className="text-base text-[#586e75] italic font-medium">
                                                    No subcategories yet.
                                                </span>
                                            )}
                                        </div>
                                    </div>

                                    <div className="flex gap-3 items-end">
                                        <div className="flex-1 space-y-2">
                                            <label className="text-sm font-bold text-[#073642]">
                                                Add New Subcategory
                                            </label>
                                            <Input
                                                placeholder={`New ${parentCategories.find(p => p.id === selectedParentId)?.name || 'Subcategory'} Type...`}
                                                value={newSubcategoryName}
                                                onChange={e => setNewSubcategoryName(e.target.value)}
                                                className="bg-[#fdf6e3] border border-[#93a1a1] h-11 text-base font-medium focus:border-[#268bd2]"
                                            />
                                        </div>
                                        <Button
                                            onClick={handleCreateSubcategory}
                                            className="bg-[#268bd2] hover:bg-[#2aa198] text-[#fdf6e3] font-bold h-11 px-6"
                                        >
                                            <Plus className="w-5 h-5 mr-2" />
                                            Add
                                        </Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Add Product Section */}
                    <div className="bg-[#eee8d5] rounded-lg border border-[#93a1a1] shadow-md">
                        <div className="bg-[#073642] px-6 py-4 rounded-t-lg border-b-2 border-[#002b36]">
                            <h2 className="font-bold text-xl text-[#fdf6e3] flex items-center gap-3">
                                <Plus className="w-6 h-6 text-[#859900]" />
                                Add New Product
                            </h2>
                        </div>

                        <div className="p-6">
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                {/* Product Details */}
                                <div className="space-y-4">
                                    <div className="space-y-2">
                                        <label className="text-sm font-bold text-[#073642]">Product Name</label>
                                        <Input
                                            type="text"
                                            placeholder="Product Name"
                                            value={newProduct.name}
                                            onChange={e => setNewProduct({ ...newProduct, name: e.target.value })}
                                            className="bg-[#fdf6e3] border border-[#93a1a1] h-11 text-base font-medium"
                                        />
                                    </div>
                                    <div className="grid grid-cols-2 gap-4">
                                        <div className="space-y-2">
                                            <label className="text-sm font-bold text-[#073642]">Price</label>
                                            <Input
                                                type="number"
                                                placeholder="Price"
                                                value={newProduct.price || ''}
                                                onChange={e => setNewProduct({ ...newProduct, price: parseFloat(e.target.value) || 0 })}
                                                className="bg-[#fdf6e3] border border-[#93a1a1] h-11 text-base font-medium"
                                            />
                                        </div>
                                        <div className="space-y-2">
                                            <label className="text-sm font-bold text-[#073642]">Quantity</label>
                                            <Input
                                                type="number"
                                                placeholder="Quantity"
                                                value={newProduct.quantityAvailable || ''}
                                                onChange={e => setNewProduct({ ...newProduct, quantityAvailable: parseInt(e.target.value) || 0 })}
                                                className="bg-[#fdf6e3] border border-[#93a1a1] h-11 text-base font-medium"
                                            />
                                        </div>
                                    </div>
                                    <div className="space-y-2">
                                        <label className="text-sm font-bold text-[#073642]">Condition</label>
                                        <select
                                            className="flex h-11 w-full rounded-md border border-[#93a1a1] bg-[#fdf6e3] px-4 py-2 text-base font-medium text-[#073642]"
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
                                    <div className="space-y-2">
                                        <label className="text-sm font-bold text-[#073642]">Description</label>
                                        <Input
                                            type="text"
                                            placeholder="Description"
                                            value={newProduct.description}
                                            onChange={e => setNewProduct({ ...newProduct, description: e.target.value })}
                                            className="bg-[#fdf6e3] border border-[#93a1a1] h-11 text-base font-medium"
                                        />
                                    </div>
                                </div>

                                {/* Category Selection */}
                                <div className="bg-[#fdf6e3] p-5 rounded-lg border border-[#93a1a1] space-y-4">
                                    <h3 className="font-bold text-base text-[#073642]">Product Category</h3>

                                    <div className="space-y-2">
                                        <label className="text-sm font-bold text-[#586e75]">1. Select Parent Category</label>
                                        <select
                                            className="flex h-11 w-full rounded-md border border-[#93a1a1] bg-[#fdf6e3] px-4 py-2 text-base font-medium"
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
                                        <label className="text-sm font-bold text-[#586e75]">2. Select Subcategory</label>
                                        <select
                                            className="flex h-11 w-full rounded-md border border-[#93a1a1] bg-[#fdf6e3] px-4 py-2 text-base font-medium disabled:opacity-50"
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
                                            <p className="text-sm text-[#dc322f] font-semibold">
                                                No subcategories found. Please add one above first.
                                            </p>
                                        )}
                                    </div>

                                    <Button
                                        onClick={addProduct}
                                        className="w-full mt-2 bg-[#859900] hover:bg-[#2aa198] h-11 font-bold text-base"
                                        disabled={newProduct.category.id === 0}
                                    >
                                        <Plus className="w-5 h-5 mr-2" />
                                        Add Product
                                    </Button>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Product List */}
                    <div className="bg-[#eee8d5] rounded-lg border border-[#93a1a1] shadow-md overflow-hidden">
                        <div className="bg-[#073642] px-6 py-4 border-b-2 border-[#002b36]">
                            <h2 className="font-bold text-xl text-[#fdf6e3]">
                                Product List ({products.length} products)
                            </h2>
                        </div>
                        <div className="divide-y-2 divide-[#93a1a1]">
                            {products.map(product => (
                                <div key={product.id} className="p-5 bg-[#fdf6e3] hover:bg-[#eee8d5] transition-colors">
                                    <div className="flex flex-wrap items-center gap-3">
                                        <div className="flex-1 min-w-0 grid grid-cols-1 sm:grid-cols-2 gap-3">
                                            <Input
                                                type="text"
                                                value={product.name}
                                                onChange={(e) => handleNameChange(product.id, e.target.value)}
                                                className="font-semibold text-base bg-[#fdf6e3] border border-[#93a1a1] h-11"
                                            />
                                            <Input
                                                type="number"
                                                value={product.price}
                                                onChange={(e) => handlePriceChange(product.id, parseFloat(e.target.value))}
                                                className="font-semibold text-base bg-[#fdf6e3] border border-[#93a1a1] h-11"
                                            />
                                        </div>
                                        <div className="flex items-center gap-2 flex-wrap">
                                            <Button
                                                size="sm"
                                                onClick={() => handleEdit(product.id, { name: product.name, price: product.price })}
                                                className="bg-[#268bd2] hover:bg-[#2aa198] h-10 font-semibold"
                                            >
                                                <Save className="w-4 h-4 mr-1.5" />
                                                Save
                                            </Button>
                                            <Button
                                                size="sm"
                                                variant="outline"
                                                onClick={() => openImageModal(product)}
                                                className="border border-[#93a1a1] h-10 font-semibold"
                                            >
                                                <ImageIcon className="w-4 h-4 mr-1.5" />
                                                Images ({product.images?.length || 0})
                                            </Button>
                                            <Button
                                                size="sm"
                                                onClick={() => deleteProduct(product.id)}
                                                className="bg-[#dc322f] hover:bg-[#cb4b16] h-10 font-semibold"
                                            >
                                                <Trash2 className="w-4 h-4 mr-1.5" />
                                                Delete
                                            </Button>
                                            <div className="flex items-center gap-2">
                                                <select
                                                    className="h-10 rounded-md border-2 border-[#93a1a1] bg-[#fdf6e3] text-[#073642] px-3 text-sm font-semibold"
                                                    value={selectedDiscount}
                                                    onChange={(e) => setSelectedDiscount(e.target.value)}
                                                >
                                                    <option value="Fixed Amount">Fixed Amount</option>
                                                    <option value="Percentage">Percentage</option>
                                                </select>
                                                <Button
                                                    size="sm"
                                                    variant="outline"
                                                    onClick={() => applyDiscount(product.id, selectedDiscount)}
                                                    className="border-2 border-[#93a1a1] h-10 font-semibold"
                                                >
                                                    <Tag className="w-4 h-4 mr-1.5" />
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

            {/* Image Management Modal */}
            {selectedProduct && (
                <ProductImagesModal
                    isOpen={imageModalOpen}
                    onClose={() => setImageModalOpen(false)}
                    productId={selectedProduct.id}
                    productName={selectedProduct.name}
                    images={selectedProduct.images || []}
                    onImagesChange={handleImagesChange}
                />
            )}
        </div>
    );
};

export default AdminPage;
