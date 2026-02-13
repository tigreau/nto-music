import React, { useState } from 'react';
import { Upload, X, Check, GripVertical, Star } from 'lucide-react';
import { DndContext, closestCenter, DragEndEvent } from '@dnd-kit/core';
import { arrayMove, SortableContext, useSortable, verticalListSortingStrategy } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';

interface ProductImage {
    id: number;
    url: string;
    altText: string;
    isPrimary: boolean;
    displayOrder: number;
}

interface Props {
    productId: number;
    images: ProductImage[];
    onImagesChange: (images: ProductImage[]) => void;
}

export function ProductImageUpload({ productId, images, onImagesChange }: Props) {
    const [uploading, setUploading] = useState(false);

    // Sort images by displayOrder before rendering
    const sortedImages = [...images].sort((a, b) => a.displayOrder - b.displayOrder);

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
        const formData = new FormData();
        formData.append('file', file);
        formData.append('isPrimary', String(isPrimary));
        formData.append('altText', file.name);

        const response = await fetch(`/api/products/${productId}/images`, {
            method: 'POST',
            credentials: 'include',
            body: formData,
        });

        if (!response.ok) throw new Error('Upload failed');
        return await response.json();
    };


    const handleDelete = async (imageId: number) => {
        if (!confirm('Delete this image?')) return;

        await fetch(`/api/products/${productId}/images/${imageId}`, {
            method: 'DELETE',
            credentials: 'include',
        });

        onImagesChange(images.filter(img => img.id !== imageId));
    };

    const handleSetPrimary = async (imageId: number) => {
        await fetch(`/api/products/${productId}/images/${imageId}/primary`, {
            method: 'PATCH',
            credentials: 'include',
        });

        onImagesChange(
            images.map(img => ({
                ...img,
                isPrimary: img.id === imageId,
            }))
        );
    };

    const handleDragEnd = async (event: DragEndEvent) => {
        const { active, over } = event;

        if (!over || active.id === over.id) return;

        const oldIndex = sortedImages.findIndex(img => img.id === active.id);
        const newIndex = sortedImages.findIndex(img => img.id === over.id);

        const reorderedImages = arrayMove(sortedImages, oldIndex, newIndex);

        // Update displayOrder property for local state consistency
        const updatedImages = reorderedImages.map((img, index) => ({
            ...img,
            displayOrder: index
        }));

        onImagesChange(updatedImages);

        // Save order to backend
        await fetch(`/api/products/${productId}/images/reorder`, {
            method: 'PUT',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(reorderedImages.map(img => img.id)),
        });
    };

    return (
        <div className="space-y-4">
            {/* Upload Area */}
            <div
                className="border-2 border-dashed border-border rounded-lg p-8 text-center hover:border-primary transition-colors cursor-pointer bg-muted/30"
                onClick={() => document.getElementById(`file-input-${productId}`)?.click()}
            >
                <Upload className="mx-auto h-12 w-12 text-muted-foreground" />
                <p className="mt-2 text-sm text-foreground">
                    Click to select images
                </p>
                <p className="text-xs text-muted-foreground mt-1">
                    PNG, JPG, WebP up to 10MB
                </p>
                <input
                    id={`file-input-${productId}`}
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
                <DndContext collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
                    <SortableContext items={sortedImages.map(img => img.id)} strategy={verticalListSortingStrategy}>
                        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                            {sortedImages.map((image) => (
                                <SortableImage
                                    key={image.id}
                                    image={image}
                                    onDelete={handleDelete}
                                    onSetPrimary={handleSetPrimary}
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
        </div>
    );
}

function SortableImage({ image, onDelete, onSetPrimary }: {
    image: ProductImage;
    onDelete: (id: number) => void;
    onSetPrimary: (id: number) => void;
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
                {...attributes}
                {...listeners}
                className="absolute top-2 left-2 z-10 p-1 bg-white/80 rounded shadow cursor-grab active:cursor-grabbing opacity-0 group-hover:opacity-100 transition-opacity"
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
            <div className="absolute inset-0 bg-black/0 group-hover:bg-black/50 transition-all flex items-center justify-center gap-2 opacity-0 group-hover:opacity-100">
                {!image.isPrimary && (
                    <button
                        onClick={() => onSetPrimary(image.id)}
                        className="p-2 bg-white rounded-full hover:bg-gray-100"
                        title="Set as primary"
                    >
                        <Check className="h-4 w-4 text-gray-700" />
                    </button>
                )}
                <button
                    onClick={() => onDelete(image.id)}
                    className="p-2 bg-white rounded-full hover:bg-red-50"
                    title="Delete image"
                >
                    <X className="h-4 w-4 text-red-600" />
                </button>
            </div>
        </div>
    );
}
