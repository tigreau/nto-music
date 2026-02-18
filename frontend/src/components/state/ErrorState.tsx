import { Button } from '@/components/ui/button';

interface ErrorStateProps {
    message?: string;
    onRetry?: () => void;
    className?: string;
}

export function ErrorState({
    message = 'Something went wrong. Please try again.',
    onRetry,
    className = '',
}: ErrorStateProps) {
    return (
        <div className={`text-center py-10 ${className}`}>
            <p className="text-destructive mb-4">{message}</p>
            {onRetry && <Button onClick={onRetry}>Try Again</Button>}
        </div>
    );
}
