import { useEffect, useMemo, useState } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Shield, Plus, Save, Trash2, Tag, FolderPlus, Image as ImageIcon } from 'lucide-react';
import { ProductImagesModal } from '@/components/admin/ProductImagesModal';
import {
    useApplyDiscount,
    useCategories,
    useCreateProduct,
    useCreateSubcategory,
    useDeleteProduct,
    useHydrateProductsWithImages,
    usePatchProduct,
    useProducts,
} from '@/hooks/useApi';
import { AdminProduct, Category, ProductCondition, ProductUpsertPayload } from '@/types';
import { toast } from 'sonner';
import { useMutationFeedback } from '@/hooks/useMutationFeedback';
import { toUnknownApiError } from '@/lib/apiError';

interface ProductEditErrorState {
    name?: string;
    price?: string;
}

type CreateProductErrorState = Partial<Record<keyof ProductUpsertPayload, string>>;
type SelectedProductSummary = { id: number; name: string };
type AdminConditionFilter = 'ALL' | ProductCondition;

const AdminPage = () => {
    const [products, setProducts] = useState<AdminProduct[]>([]);
    const [persistedProductDrafts, setPersistedProductDrafts] = useState<Record<number, { name: string; price: number }>>({});
    const [editErrors, setEditErrors] = useState<Record<number, ProductEditErrorState>>({});
    const [createProductErrors, setCreateProductErrors] = useState<CreateProductErrorState>({});
    const [adminSearch, setAdminSearch] = useState('');
    const [adminCondition, setAdminCondition] = useState<AdminConditionFilter>('ALL');

    const [selectedParentId, setSelectedParentId] = useState<number>(0);
    const [newSubcategoryName, setNewSubcategoryName] = useState('');

    const [newProduct, setNewProduct] = useState<ProductUpsertPayload>({
        name: '',
        price: 0,
        description: '',
        quantityAvailable: 0,
        condition: 'GOOD' as ProductCondition,
        categoryId: 0,
        conditionNotes: '',
    });
    const [productParentId, setProductParentId] = useState<number>(0);

    const [selectedDiscount, setSelectedDiscount] = useState('Fixed Amount');

    const [imageModalOpen, setImageModalOpen] = useState(false);
    const [selectedProduct, setSelectedProduct] = useState<SelectedProductSummary | null>(null);

    const { data: allCategories = [] } = useCategories();
    const { data: productsPage } = useProducts({ page: 0, size: 200, sort: 'recommended' });

    const createSubcategoryMutation = useCreateSubcategory();
    const createProductMutation = useCreateProduct();
    const patchProductMutation = usePatchProduct();
    const deleteProductMutation = useDeleteProduct();
    const applyDiscountMutation = useApplyDiscount();
    const hydrateProductsWithImagesMutation = useHydrateProductsWithImages();
    const runWithFeedback = useMutationFeedback();

    useEffect(() => {
        if (allCategories.length > 0) {
            if (selectedParentId === 0) setSelectedParentId(allCategories[0].id);
            if (productParentId === 0) setProductParentId(allCategories[0].id);
        }
    }, [allCategories, selectedParentId, productParentId]);

    useEffect(() => {
        const items = productsPage?.content ?? [];
        setProducts((prev) => {
            const previousImagesById = new Map(prev.map((product) => [product.id, product.images]));
            return items.map((item) => ({
                id: item.id,
                slug: item.slug,
                name: item.name,
                price: item.price,
                description: '',
                condition: item.condition,
                quantityAvailable: 0,
                category: null,
                images: previousImagesById.get(item.id) ?? item.images ?? [],
            }));
        });
        setPersistedProductDrafts(
            Object.fromEntries(items.map((item) => [item.id, { name: item.name, price: item.price }]))
        );
        setEditErrors({});
    }, [productsPage, hydrateProductsWithImagesMutation]);

    useEffect(() => {
        const ids = (productsPage?.content ?? [])
            .filter((item) => {
                if (!Array.isArray(item.images)) return true;
                return item.images.length === 0 && !!item.thumbnailUrl;
            })
            .map((item) => item.id);
        if (ids.length === 0) return;
        let cancelled = false;

        hydrateProductsWithImagesMutation.mutateAsync(ids).then((rows) => {
            if (cancelled) return;
            const imagesById = new Map(rows.map((row) => [row.id, row.images]));
            setProducts((prev) =>
                prev.map((product) => ({
                    ...product,
                    images: imagesById.get(product.id) ?? product.images,
                })),
            );
        });

        return () => {
            cancelled = true;
        };
    }, [productsPage, hydrateProductsWithImagesMutation]);

    const parseValidationFieldError = (error: unknown): { field: string | null; message: string } => {
        const message = toUnknownApiError(error).message;
        const match = message.match(/^([a-zA-Z][\w.]*)\s*:\s*(.+)$/);
        if (!match) {
            return { field: null, message };
        }
        const rawField = match[1].toLowerCase().split('.').pop() || match[1].toLowerCase();
        const field = rawField || null;
        return { field, message: match[2] || message };
    };

    const handleCreateSubcategory = async () => {
        if (!newSubcategoryName || !selectedParentId) {
            toast.error('Please provide a name and select a parent category.');
            return;
        }

        await runWithFeedback(
            () => createSubcategoryMutation.mutateAsync({
                parentId: selectedParentId,
                name: newSubcategoryName,
            }),
            {
                context: 'admin.createSubcategory',
                successMessage: 'Subcategory created',
                onSuccess: () => setNewSubcategoryName(''),
            },
        );
    };

    const addProduct = async () => {
        if (newProduct.categoryId === 0) {
            toast.error('Please select a valid subcategory.');
            return;
        }

        await runWithFeedback(
            () => createProductMutation.mutateAsync(newProduct),
            {
                context: 'admin.createProduct',
                successMessage: 'Product added successfully',
                onSuccess: () => {
                    setNewProduct({
                        name: '',
                        price: 0,
                        description: '',
                        quantityAvailable: 0,
                        condition: 'GOOD',
                        categoryId: 0,
                        conditionNotes: '',
                    });
                    setCreateProductErrors({});
                },
                onError: (error) => {
                    const parsed = parseValidationFieldError(error);
                    if (!parsed.field) return;
                    const knownFields: (keyof ProductUpsertPayload)[] = [
                        'name',
                        'description',
                        'price',
                        'quantityAvailable',
                        'categoryId',
                        'condition',
                        'conditionNotes',
                    ];
                    if (knownFields.includes(parsed.field as keyof ProductUpsertPayload)) {
                        setCreateProductErrors((prev) => ({
                            ...prev,
                            [parsed.field as keyof ProductUpsertPayload]: parsed.message,
                        }));
                    }
                },
            },
        );
    };

    const handleEdit = async (id: number, updatedProduct: Partial<AdminProduct>) => {
        await runWithFeedback(
            () => patchProductMutation.mutateAsync({ id, data: updatedProduct }),
            {
                context: 'admin.updateProduct',
                successMessage: 'Product updated',
                onSuccess: (savedProduct) => {
                    setPersistedProductDrafts((prev) => ({
                        ...prev,
                        [id]: { name: savedProduct.name, price: savedProduct.price },
                    }));
                    setProducts((prev) => prev.map((product) =>
                        product.id === id
                            ? { ...product, name: savedProduct.name, price: savedProduct.price }
                            : product
                    ));
                    setEditErrors((prev) => {
                        const next = { ...prev };
                        delete next[id];
                        return next;
                    });
                },
                onError: (error) => {
                    const parsed = parseValidationFieldError(error);
                    const persisted = persistedProductDrafts[id];
                    if (persisted) {
                        setProducts((prev) => prev.map((product) =>
                            product.id === id
                                ? { ...product, name: persisted.name, price: persisted.price }
                                : product
                        ));
                    }
                    if (parsed.field) {
                        const field = parsed.field;
                        setEditErrors((prev) => ({
                            ...prev,
                            [id]: { [field]: parsed.message },
                        }));
                    }
                },
            },
        );
    };

    const removeProduct = async (id: number) => {
        await runWithFeedback(
            () => deleteProductMutation.mutateAsync(id),
            {
                context: 'admin.deleteProduct',
                successMessage: 'Product deleted',
            },
        );
    };

    const applyDiscount = async (id: number, discountType: string) => {
        await runWithFeedback(
            () => applyDiscountMutation.mutateAsync({ id, discountType }),
            {
                context: 'admin.applyDiscount',
                successMessage: 'Discount applied',
            },
        );
    };

    const handleNameChange = (id: number, newName: string) => {
        setProducts(products.map((product) =>
            product.id === id ? { ...product, name: newName } : product
        ));
        setEditErrors((prev) => {
            const current = prev[id];
            if (!current?.name) return prev;
            const next = { ...prev };
            next[id] = { ...current, name: undefined };
            if (!next[id].price) delete next[id];
            return next;
        });
    };

    const handlePriceChange = (id: number, newPrice: number) => {
        setProducts(products.map((product) =>
            product.id === id ? { ...product, price: newPrice } : product
        ));
        setEditErrors((prev) => {
            const current = prev[id];
            if (!current?.price) return prev;
            const next = { ...prev };
            next[id] = { ...current, price: undefined };
            if (!next[id].name) delete next[id];
            return next;
        });
    };

    const clearCreateFieldError = (field: keyof ProductUpsertPayload) => {
        setCreateProductErrors((prev) => {
            if (!prev[field]) return prev;
            const next = { ...prev };
            delete next[field];
            return next;
        });
    };

    const getSubcategories = (parentId: number) => {
        const parent = allCategories.find((p) => p.id === parentId);
        return parent?.subCategories || [];
    };

    const openImageModal = (product: AdminProduct) => {
        setSelectedProduct({ id: product.id, name: product.name });
        setImageModalOpen(true);
    };

    const handleImagesChange = (images: AdminProduct['images']) => {
        if (selectedProduct) {
            const updatedProducts = products.map((p) =>
                p.id === selectedProduct.id ? { ...p, images } : p
            );
            setProducts(updatedProducts);
        }
    };

    const filteredProducts = useMemo(() => {
        const search = adminSearch.trim().toLowerCase();
        return products.filter((product) => {
            const matchesSearch = !search
                || product.name.toLowerCase().includes(search)
                || product.slug.toLowerCase().includes(search);
            const matchesCondition = adminCondition === 'ALL' || product.condition === adminCondition;
            return matchesSearch && matchesCondition;
        });
    }, [products, adminSearch, adminCondition]);

    return (
        <div className="min-h-screen bg-background">
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
                    <div className="bg-[#eee8d5] rounded-lg border border-[#93a1a1] shadow-md">
                        <div className="bg-[#073642] px-6 py-4 rounded-t-lg border-b-2 border-[#002b36]">
                            <div className="flex items-center gap-3">
                                <FolderPlus className="w-6 h-6 text-[#2aa198]" />
                                <h2 className="font-bold text-xl text-[#fdf6e3]">Manage Categories</h2>
                            </div>
                        </div>

                        <div className="p-6">
                            <div className="grid grid-cols-1 md:grid-cols-12 gap-6">
                                <div className="md:col-span-4 space-y-3">
                                    <label className="text-sm font-bold text-[#073642] uppercase tracking-wide">
                                        Select Parent Category
                                    </label>
                                    <select
                                        className="flex h-11 w-full rounded-md border border-[#93a1a1] bg-[#fdf6e3] px-4 py-2 text-base font-medium text-[#073642] ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#268bd2] focus-visible:border-[#268bd2]"
                                        value={selectedParentId}
                                        onChange={e => setSelectedParentId(parseInt(e.target.value))}
                                    >
                                        {allCategories.map((cat: Category) => (
                                            <option key={cat.id} value={cat.id}>{cat.name}</option>
                                        ))}
                                    </select>
                                    <p className="text-sm text-[#586e75] font-medium">
                                        Select a parent category to view or add subcategories.
                                    </p>
                                </div>

                                <div className="md:col-span-8 space-y-4">
                                    <div className="bg-[#fdf6e3] rounded-md p-4 border border-[#93a1a1]">
                                        <label className="text-xs font-bold text-[#586e75] uppercase mb-3 block tracking-wide">
                                            Existing Subcategories
                                        </label>
                                        <div className="flex flex-wrap gap-2">
                                            {getSubcategories(selectedParentId).length > 0 ? (
                                                getSubcategories(selectedParentId).map((sub: Category) => (
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
                                                placeholder={`New ${allCategories.find(p => p.id === selectedParentId)?.name || 'Subcategory'} Type...`}
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

                    <div className="bg-[#eee8d5] rounded-lg border border-[#93a1a1] shadow-md">
                        <div className="bg-[#073642] px-6 py-4 rounded-t-lg border-b-2 border-[#002b36]">
                            <h2 className="font-bold text-xl text-[#fdf6e3] flex items-center gap-3">
                                <Plus className="w-6 h-6 text-[#859900]" />
                                Add New Product
                            </h2>
                        </div>

                        <div className="p-6">
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div className="space-y-4">
                                    <div className="space-y-2">
                                        <label className="text-sm font-bold text-[#073642]">Product Name</label>
                                        <Input
                                            type="text"
                                            placeholder="Product Name"
                                            value={newProduct.name}
                                            onChange={e => {
                                                setNewProduct({ ...newProduct, name: e.target.value });
                                                clearCreateFieldError('name');
                                            }}
                                            className={`bg-[#fdf6e3] border h-11 text-base font-medium ${createProductErrors.name ? 'border-[#dc322f] focus-visible:ring-[#dc322f]' : 'border-[#93a1a1]'}`}
                                        />
                                        {createProductErrors.name && (
                                            <p className="text-xs font-semibold text-[#dc322f]">{createProductErrors.name}</p>
                                        )}
                                    </div>
                                    <div className="grid grid-cols-2 gap-4">
                                        <div className="space-y-2">
                                            <label className="text-sm font-bold text-[#073642]">Price</label>
                                            <Input
                                                type="number"
                                                placeholder="Price"
                                                value={newProduct.price || ''}
                                                onChange={e => {
                                                    setNewProduct({ ...newProduct, price: parseFloat(e.target.value) || 0 });
                                                    clearCreateFieldError('price');
                                                }}
                                                className={`bg-[#fdf6e3] border h-11 text-base font-medium ${createProductErrors.price ? 'border-[#dc322f] focus-visible:ring-[#dc322f]' : 'border-[#93a1a1]'}`}
                                            />
                                            {createProductErrors.price && (
                                                <p className="text-xs font-semibold text-[#dc322f]">{createProductErrors.price}</p>
                                            )}
                                        </div>
                                        <div className="space-y-2">
                                            <label className="text-sm font-bold text-[#073642]">Quantity</label>
                                            <Input
                                                type="number"
                                                placeholder="Quantity"
                                                value={newProduct.quantityAvailable || ''}
                                                onChange={e => {
                                                    setNewProduct({ ...newProduct, quantityAvailable: parseInt(e.target.value) || 0 });
                                                    clearCreateFieldError('quantityAvailable');
                                                }}
                                                className={`bg-[#fdf6e3] border h-11 text-base font-medium ${createProductErrors.quantityAvailable ? 'border-[#dc322f] focus-visible:ring-[#dc322f]' : 'border-[#93a1a1]'}`}
                                            />
                                            {createProductErrors.quantityAvailable && (
                                                <p className="text-xs font-semibold text-[#dc322f]">{createProductErrors.quantityAvailable}</p>
                                            )}
                                        </div>
                                    </div>
                                    <div className="space-y-2">
                                        <label className="text-sm font-bold text-[#073642]">Condition</label>
                                        <select
                                            className={`flex h-11 w-full rounded-md border bg-[#fdf6e3] px-4 py-2 text-base font-medium text-[#073642] ${createProductErrors.condition ? 'border-[#dc322f]' : 'border-[#93a1a1]'}`}
                                            value={newProduct.condition}
                                            onChange={e => {
                                                setNewProduct({ ...newProduct, condition: e.target.value as ProductCondition });
                                                clearCreateFieldError('condition');
                                            }}
                                        >
                                            <option value="EXCELLENT">Excellent</option>
                                            <option value="GOOD">Good</option>
                                            <option value="FAIR">Fair</option>
                                        </select>
                                        {createProductErrors.condition && (
                                            <p className="text-xs font-semibold text-[#dc322f]">{createProductErrors.condition}</p>
                                        )}
                                    </div>
                                    <div className="space-y-2">
                                        <label className="text-sm font-bold text-[#073642]">Description</label>
                                        <Input
                                            type="text"
                                            placeholder="Description"
                                            value={newProduct.description}
                                            onChange={e => {
                                                setNewProduct({ ...newProduct, description: e.target.value });
                                                clearCreateFieldError('description');
                                            }}
                                            className={`bg-[#fdf6e3] border h-11 text-base font-medium ${createProductErrors.description ? 'border-[#dc322f] focus-visible:ring-[#dc322f]' : 'border-[#93a1a1]'}`}
                                        />
                                        {createProductErrors.description && (
                                            <p className="text-xs font-semibold text-[#dc322f]">{createProductErrors.description}</p>
                                        )}
                                    </div>
                                </div>

                                <div className="bg-[#fdf6e3] p-5 rounded-lg border border-[#93a1a1] space-y-4">
                                    <h3 className="font-bold text-base text-[#073642]">Product Category</h3>

                                    <div className="space-y-2">
                                        <label className="text-sm font-bold text-[#586e75]">1. Select Parent Category</label>
                                        <select
                                            className="flex h-11 w-full rounded-md border border-[#93a1a1] bg-[#fdf6e3] px-4 py-2 text-base font-medium"
                                            value={productParentId}
                                            onChange={e => {
                                                setProductParentId(parseInt(e.target.value));
                                                setNewProduct(prev => ({ ...prev, categoryId: 0 }));
                                            }}
                                        >
                                            {allCategories.map((cat: Category) => (
                                                <option key={cat.id} value={cat.id}>{cat.name}</option>
                                            ))}
                                        </select>
                                    </div>

                                    <div className="space-y-2">
                                        <label className="text-sm font-bold text-[#586e75]">2. Select Subcategory</label>
                                        <select
                                            className={`flex h-11 w-full rounded-md border bg-[#fdf6e3] px-4 py-2 text-base font-medium disabled:opacity-50 ${createProductErrors.categoryId ? 'border-[#dc322f]' : 'border-[#93a1a1]'}`}
                                            value={newProduct.categoryId}
                                            onChange={e => {
                                                setNewProduct({ ...newProduct, categoryId: parseInt(e.target.value) });
                                                clearCreateFieldError('categoryId');
                                            }}
                                            disabled={getSubcategories(productParentId).length === 0}
                                        >
                                            <option value="0">-- Select Subcategory --</option>
                                            {getSubcategories(productParentId).map((cat: Category) => (
                                                <option key={cat.id} value={cat.id}>{cat.name}</option>
                                            ))}
                                        </select>
                                        {createProductErrors.categoryId && (
                                            <p className="text-xs font-semibold text-[#dc322f]">{createProductErrors.categoryId}</p>
                                        )}
                                        {getSubcategories(productParentId).length === 0 && (
                                            <p className="text-sm text-[#dc322f] font-semibold">
                                                No subcategories found. Please add one above first.
                                            </p>
                                        )}
                                    </div>

                                    <Button
                                        onClick={addProduct}
                                        className="w-full mt-2 bg-[#859900] hover:bg-[#2aa198] h-11 font-bold text-base"
                                        disabled={newProduct.categoryId === 0}
                                    >
                                        <Plus className="w-5 h-5 mr-2" />
                                        Add Product
                                    </Button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="bg-[#eee8d5] rounded-lg border border-[#93a1a1] shadow-md overflow-hidden">
                        <div className="bg-[#073642] px-6 py-4 border-b-2 border-[#002b36]">
                            <h2 className="font-bold text-xl text-[#fdf6e3]">
                                Product List ({filteredProducts.length} of {products.length} products)
                            </h2>
                        </div>
                        <div className="p-5 bg-[#fdf6e3] border-b border-[#93a1a1]">
                            <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                                <Input
                                    type="search"
                                    value={adminSearch}
                                    onChange={(e) => setAdminSearch(e.target.value)}
                                    placeholder="Filter by product name or slug..."
                                    className="bg-[#fdf6e3] border border-[#93a1a1] h-11 text-base font-medium"
                                />
                                <select
                                    value={adminCondition}
                                    onChange={(e) => setAdminCondition(e.target.value as AdminConditionFilter)}
                                    className="flex h-11 w-full rounded-md border border-[#93a1a1] bg-[#fdf6e3] px-4 py-2 text-base font-medium text-[#073642]"
                                >
                                    <option value="ALL">All conditions</option>
                                    <option value="EXCELLENT">Excellent</option>
                                    <option value="GOOD">Good</option>
                                    <option value="FAIR">Fair</option>
                                </select>
                                <Button
                                    type="button"
                                    variant="outline"
                                    onClick={() => {
                                        setAdminSearch('');
                                        setAdminCondition('ALL');
                                    }}
                                    className="h-11 border border-[#93a1a1] font-semibold"
                                >
                                    Clear filters
                                </Button>
                            </div>
                        </div>
                        <div className="divide-y-2 divide-[#93a1a1]">
                            {filteredProducts.map(product => (
                                <div key={product.id} className="p-5 bg-[#fdf6e3] hover:bg-[#eee8d5] transition-colors">
                                    <div className="flex flex-wrap items-center gap-3">
                                        <div className="flex-1 min-w-0 grid grid-cols-1 sm:grid-cols-2 gap-3">
                                            <div className="space-y-1">
                                                <Input
                                                    type="text"
                                                    value={product.name}
                                                    onChange={(e) => handleNameChange(product.id, e.target.value)}
                                                    className={`font-semibold text-base bg-[#fdf6e3] border h-11 ${editErrors[product.id]?.name ? 'border-[#dc322f] focus-visible:ring-[#dc322f]' : 'border-[#93a1a1]'}`}
                                                />
                                                {editErrors[product.id]?.name && (
                                                    <p className="text-xs font-semibold text-[#dc322f]">
                                                        {editErrors[product.id].name}
                                                    </p>
                                                )}
                                            </div>
                                            <div className="space-y-1">
                                                <Input
                                                    type="number"
                                                    value={product.price}
                                                    onChange={(e) => handlePriceChange(product.id, parseFloat(e.target.value))}
                                                    className={`font-semibold text-base bg-[#fdf6e3] border h-11 ${editErrors[product.id]?.price ? 'border-[#dc322f] focus-visible:ring-[#dc322f]' : 'border-[#93a1a1]'}`}
                                                />
                                                {editErrors[product.id]?.price && (
                                                    <p className="text-xs font-semibold text-[#dc322f]">
                                                        {editErrors[product.id].price}
                                                    </p>
                                                )}
                                            </div>
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
                                                onClick={() => removeProduct(product.id)}
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
                            {filteredProducts.length === 0 && (
                                <div className="p-8 text-center text-[#586e75] font-medium">
                                    No products match the current admin filters.
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>

            {selectedProduct && (
                <ProductImagesModal
                    isOpen={imageModalOpen}
                    onClose={() => {
                        setImageModalOpen(false);
                        setSelectedProduct(null);
                    }}
                    productId={selectedProduct.id}
                    productName={selectedProduct.name}
                    onImagesChange={handleImagesChange}
                />
            )}
        </div>
    );
};

export default AdminPage;
