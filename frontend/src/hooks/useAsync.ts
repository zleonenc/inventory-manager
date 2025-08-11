import {
    useCallback,
    useState
} from "react";

export function useAsync<TArgs extends any[] = any[], TResult = any>(fn: (...args: TArgs) => Promise<TResult> | TResult) {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const run = useCallback(async (...args: TArgs): Promise<TResult> => {
        setLoading(true);
        setError(null);
        try {
            const result = await fn(...args);
            return result as TResult;
        } catch (err: any) {
            setError(err?.message || "Unexpected error");
            throw err;
        } finally {
            setLoading(false);
        }
    }, [fn]);

    return { run, loading, error, setError } as const;
}
