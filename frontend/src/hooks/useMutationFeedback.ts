import { useCallback } from 'react';
import { toast } from 'sonner';
import { getApiErrorPolicy } from '@/lib/apiError';

interface MutationFeedbackOptions<T> {
  successMessage?: string;
  fallbackErrorMessage?: string;
  context: string;
  onSuccess?: (result: T) => void | Promise<void>;
  onError?: (error: unknown) => void | Promise<void>;
}

export function useMutationFeedback() {
  return useCallback(async <T>(
    execute: () => Promise<T>,
    options: MutationFeedbackOptions<T>,
  ): Promise<T | null> => {
    try {
      const result = await execute();
      if (options.successMessage) {
        toast.success(options.successMessage);
      }
      if (options.onSuccess) {
        await options.onSuccess(result);
      }
      return result;
    } catch (error) {
      console.error(`[mutation:${options.context}]`, error);
      toast.error(options.fallbackErrorMessage ?? getApiErrorPolicy(error).message);
      if (options.onError) {
        await options.onError(error);
      }
      return null;
    }
  }, []);
}
