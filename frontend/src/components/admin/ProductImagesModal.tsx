
import { useEffect, useRef, useState } from 'react';
import { X } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { ProductImageUpload } from './ProductImageUpload';
import { useProduct } from '@/hooks/useApi';
import { ProductImage } from '@/types';

interface Props {
    isOpen: boolean;
    onClose: () => void;
    productId: number;
    productName: string;
    onImagesChange: (images: ProductImage[]) => void;
}

export function ProductImagesModal({
    isOpen,
    onClose,
    productId,
    productName,
    onImagesChange
}: Props) {
    const [modalImages, setModalImages] = useState<ProductImage[]>([]);
    const lastSyncedKeyRef = useRef<string>('');
    const onImagesChangeRef = useRef(onImagesChange);
    const { data: product, isFetching } = useProduct(isOpen ? productId : null);

    useEffect(() => {
        onImagesChangeRef.current = onImagesChange;
    }, [onImagesChange]);

    useEffect(() => {
        if (!isOpen) return;
        setModalImages([]);
        lastSyncedKeyRef.current = '';
    }, [isOpen, productId]);

    useEffect(() => {
        if (!isOpen || !product || isFetching) return;
        const nextImages = product.images ?? [];
        setModalImages(nextImages);
        const signature = nextImages
            .map((image) => `${image.id}:${image.displayOrder}:${image.isPrimary ? 1 : 0}`)
            .join('|');
        const syncKey = `${productId}:${signature}`;
        if (lastSyncedKeyRef.current !== syncKey) {
            lastSyncedKeyRef.current = syncKey;
            onImagesChangeRef.current(nextImages);
        }
    }, [isOpen, product, productId, isFetching]);

    const handleImagesChange = (images: ProductImage[]) => {
        setModalImages(images);
        onImagesChange(images);
    };

    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50">
            <div className="bg-card rounded-xl border border-border w-full max-w-4xl max-h-[90vh] overflow-hidden flex flex-col">
                {/* Header */}
                <div className="flex items-center justify-between p-6 border-b border-border">
                    <div>
                        <h2 className="text-xl font-semibold text-foreground">
                            Manage Images
                        </h2>
                        <p className="text-sm text-muted-foreground mt-1">
                            {productName}
                        </p>
                    </div>
                    <button
                        onClick={onClose}
                        className="p-2 hover:bg-muted rounded-lg transition-colors"
                    >
                        <X className="h-5 w-5" />
                    </button>
                </div>

                {/* Content */}
                <div className="flex-1 overflow-y-auto p-6">
                    <ProductImageUpload
                        productId={productId}
                        images={modalImages}
                        onImagesChange={handleImagesChange}
                    />
                </div>

                {/* Footer */}
                <div className="p-6 border-t border-border flex justify-end gap-3">
                    <Button variant="outline" onClick={onClose}>
                        Close
                    </Button>
                </div>
            </div>
        </div>
    );
}
