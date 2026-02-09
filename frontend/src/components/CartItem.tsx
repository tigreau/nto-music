import { Button } from "@/components/ui/button";
import { Trash2 } from "lucide-react";

interface CartItemData {
    id: number;
    product: {
        name: string;
        price: number;
    };
    quantity: number;
}

interface CartItemProps {
    cartItem: CartItemData;
    deleteCartItem: (id: number) => void;
}

const CartItem = ({ cartItem, deleteCartItem }: CartItemProps) => {
    return (
        <li className="flex items-center justify-between p-4 bg-card rounded-lg border border-border">
            <div className="flex items-center gap-4">
                {/* Product Image Placeholder */}
                <div className="w-16 h-16 bg-muted rounded-md flex items-center justify-center">
                    <div className="w-10 h-10 bg-gradient-to-br from-muted-foreground/20 to-muted-foreground/10 rounded" />
                </div>
                <div>
                    <h3 className="font-medium text-foreground">{cartItem.product.name}</h3>
                    <p className="text-sm text-muted-foreground">
                        {cartItem.product.price} EUR × {cartItem.quantity}
                    </p>
                </div>
            </div>
            <div className="flex items-center gap-4">
                <p className="font-semibold text-primary">
                    {(cartItem.product.price * cartItem.quantity).toFixed(2)} EUR
                </p>
                <Button
                    variant="ghost"
                    size="icon"
                    className="text-destructive hover:text-destructive hover:bg-destructive/10"
                    onClick={() => deleteCartItem(cartItem.id)}
                >
                    <Trash2 className="w-4 h-4" />
                </Button>
            </div>
        </li>
    );
};

export default CartItem;
