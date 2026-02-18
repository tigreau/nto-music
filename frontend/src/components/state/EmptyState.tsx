import { ReactNode } from 'react';

interface EmptyStateProps {
    title: string;
    description?: string;
    icon?: ReactNode;
    action?: ReactNode;
    className?: string;
}

export function EmptyState({ title, description, icon, action, className = '' }: EmptyStateProps) {
    return (
        <div className={`bg-card rounded-xl border border-border p-12 text-center ${className}`}>
            {icon}
            <h2 className="text-xl font-semibold text-foreground mb-2">{title}</h2>
            {description && <p className="text-muted-foreground mb-6">{description}</p>}
            {action}
        </div>
    );
}
