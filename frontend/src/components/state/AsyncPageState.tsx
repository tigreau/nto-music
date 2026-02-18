import { ReactNode } from 'react';
import { ErrorState } from '@/components/state/ErrorState';
import { LoadingState } from '@/components/state/LoadingState';

interface AsyncPageStateProps {
  isLoading?: boolean;
  isError?: boolean;
  errorMessage?: string;
  onRetry?: () => void;
  loadingMessage?: string;
  loadingClassName?: string;
  empty?: boolean;
  emptyState?: ReactNode;
  children: ReactNode;
}

export function AsyncPageState({
  isLoading = false,
  isError = false,
  errorMessage,
  onRetry,
  loadingMessage,
  loadingClassName,
  empty = false,
  emptyState,
  children,
}: AsyncPageStateProps) {
  if (isLoading) {
    return <LoadingState message={loadingMessage} className={loadingClassName} />;
  }

  if (isError) {
    return <ErrorState message={errorMessage} onRetry={onRetry} className={loadingClassName} />;
  }

  if (empty && emptyState) {
    return <>{emptyState}</>;
  }

  return <>{children}</>;
}
