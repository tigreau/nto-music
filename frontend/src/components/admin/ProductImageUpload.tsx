import { useRef, useState } from 'react';
import { Upload, X, GripVertical, Star } from 'lucide-react';
import { DndContext, closestCenter, DragEndEvent, PointerSensor, useSensor, useSensors } from '@dnd-kit/core';
import { arrayMove, rectSortingStrategy, SortableContext, useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { toast } from 'sonner';
import { getApiErrorPolicy } from '@/lib/apiError';
import { normalizeImagesAfterDelete } from '@/components/admin/productImageState';
import {
    useDeleteProductImage,
    useReorderProductImages,
    useUploadProductImage,
} from '@/hooks/useApi';
import { ProductImage } from '@/types';

interface Props {
    productId: number;
    images: ProductImage[];
    onImagesChange: (images: ProductImage[]) => void;
}

export function ProductImageUpload({ productId, images, onImagesChange }: Props) {
    const [uploading, setUploading] = useState(false);
    const [reordering, setReordering] = useState(false);
    const fileInputRef = useRef<HTMLInputElement | null>(null);
    const uploadImageMutation = useUploadProductImage();
    const deleteImageMutation = useDeleteProductImage();
    const reorderImagesMutation = useReorderProductImages();

    // Sort images by displayOrder before rendering
    const sortedImages = [...images].sort((a, b) => a.displayOrder - b.displayOrder);
    const sensors = useSensors(
        useSensor(PointerSensor, {
            activationConstraint: {
                distance: 6,
            },
        }),
    );

    const handleFileSelect = async (files: FileList | null) => {
        if (!files || files.length === 0) return;

        setUploading(true);

        try {
            // Optimistic Update: Create placeholders or just wait for server?
            // User plan was to loop. Let's stick to accumulating to update parent once.
            let currentImages = [...images];
            for (let i = 0; i < files.length; i++) {
                const file = files[i];
                // First image is primary if no images exist yet
                const isPrimary = i === 0 && currentImages.length === 0;
                const newImage = await uploadImage(file, isPrimary);
                currentImages = [...currentImages, newImage];
            }
            onImagesChange(currentImages);

        } finally {
            setUploading(false);
        }
    };

    const uploadImage = async (file: File, isPrimary: boolean) => {
        try {
            return await uploadImageMutation.mutateAsync({ productId, file, altText: file.name, isPrimary });
        } catch (error) {
            toast.error(getApiErrorPolicy(error).message);
            throw error;
        }
    };


    const handleDelete = async (imageId: number) => {
        try {
            await deleteImageMutation.mutateAsync({ productId, imageId });
            const nextImages = normalizeImagesAfterDelete(images, imageId);
            onImagesChange(nextImages);
            toast.success('Image deleted');
        } catch (error) {
            console.error('Failed to delete product image', error);
            toast.error(getApiErrorPolicy(error).message);
        }
    };

    const handleDragEnd = async (event: DragEndEvent) => {
        const { active, over } = event;

        if (!over || active.id === over.id) return;

        const oldIndex = sortedImages.findIndex(img => String(img.id) === String(active.id));
        const newIndex = sortedImages.findIndex(img => String(img.id) === String(over.id));
        if (oldIndex < 0 || newIndex < 0) return;

        const reorderedImages = arrayMove(sortedImages, oldIndex, newIndex);

        // Update displayOrder property for local state consistency
        const updatedImages = reorderedImages.map((img, index) => ({
            ...img,
            displayOrder: index,
            isPrimary: index === 0,
        }));

        onImagesChange(updatedImages);

        // Save order to backend; rollback local state if it fails.
        setReordering(true);
        try {
            await reorderImagesMutation.mutateAsync({ productId, imageIds: reorderedImages.map(img => img.id) });
            toast.success('Image order updated');
        } catch (error) {
            onImagesChange(sortedImages);
            console.error('Failed to reorder product images', error);
            toast.error(getApiErrorPolicy(error).message);
        } finally {
            setReordering(false);
        }
    };

    return (
        <div className="space-y-4">
            <p className="text-sm text-muted-foreground">
                Drag images to reorder. The first image becomes primary.
            </p>
            {/* Upload Area */}
            <div
                className="border-2 border-dashed border-border rounded-lg p-8 text-center hover:border-primary transition-colors cursor-pointer bg-muted/30"
                onClick={() => fileInputRef.current?.click()}
            >
                <Upload className="mx-auto h-12 w-12 text-muted-foreground" />
                <p className="mt-2 text-sm text-foreground">
                    Click to select images
                </p>
                <p className="text-xs text-muted-foreground mt-1">
                    PNG, JPG, WebP up to 10MB
                </p>
                <input
                    ref={fileInputRef}
                    type="file"
                    multiple
                    accept="image/*"
                    className="hidden"
                    onChange={(e) => handleFileSelect(e.target.files)}
                    disabled={uploading}
                />
            </div>

            {/* Image Grid with DndContext */}
            {sortedImages.length > 0 && (
                <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
                    <SortableContext items={sortedImages.map(img => img.id)} strategy={rectSortingStrategy}>
                        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                            {sortedImages.map((image) => (
                                <SortableImage
                                    key={image.id}
                                    image={image}
                                    onDelete={handleDelete}
                                />
                            ))}
                        </div>
                    </SortableContext>
                </DndContext>
            )}

            {uploading && (
                <div className="text-center py-4">
                    <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-primary" />
                    <p className="mt-2 text-sm text-muted-foreground">Uploading...</p>
                </div>
            )}
            {reordering && (
                <p className="text-sm text-muted-foreground text-center">Saving image order...</p>
            )}
        </div>
    );
}

function SortableImage({ image, onDelete }: {
    image: ProductImage;
    onDelete: (id: number) => void;
}) {
    const {
        attributes,
        listeners,
        setNodeRef,
        transform,
        transition,
        isDragging,
    } = useSortable({ id: image.id });

    const style = {
        transform: CSS.Transform.toString(transform),
        transition,
        opacity: isDragging ? 0.5 : 1,
        zIndex: isDragging ? 10 : 0
    };

    return (
        <div
            ref={setNodeRef}
            style={style}
            className="relative group bg-card rounded-lg shadow-sm overflow-hidden border border-border"
        >
            {/* Drag Handle */}
            <button
                type="button"
                {...attributes}
                {...listeners}
                onClick={(e) => e.preventDefault()}
                className="absolute top-2 left-2 z-10 p-1.5 bg-[#eee8d5]/90 rounded shadow cursor-grab active:cursor-grabbing touch-none"
                title="Drag to reorder"
            >
                <GripVertical className="h-4 w-4 text-foreground" />
            </button>

            {/* Image */}
            <img
                src={image.url}
                alt={image.altText}
                className="w-full h-32 object-cover"
            />

            {/* Primary Badge */}
            {image.isPrimary && (
                <div className="absolute top-2 right-2 px-2 py-1 bg-primary text-primary-foreground text-xs font-semibold rounded flex items-center gap-1 z-10">
                    <Star className="h-3 w-3" />
                    Primary
                </div>
            )}

            {/* Actions */}
            <div className="absolute bottom-2 right-2">
                <button
                    type="button"
                    onClick={() => onDelete(image.id)}
                    className="p-2 bg-[#eee8d5] rounded-full hover:bg-[#fdf6e3]"
                    title="Delete image"
                >
                    <X className="h-4 w-4 text-red-600" />
                </button>
            </div>
        </div>
    );
}
