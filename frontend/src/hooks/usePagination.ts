import {
    useCallback,
    useMemo,
    useState
} from "react";

type Options = {
    total: number;
    initialPage?: number;
    initialRowsPerPage?: number;
    rowsPerPageOptions?: number[];
};

export function usePagination({
    total,
    initialPage = 0,
    initialRowsPerPage = 10,
    rowsPerPageOptions = [5, 10, 25, 50],
}: Options) {
    const [page, setPage] = useState(initialPage);
    const [rowsPerPage, setRowsPerPage] = useState(initialRowsPerPage);

    const pageCount = useMemo(() => Math.max(1, Math.ceil((total || 0) / (rowsPerPage || 1))), [total, rowsPerPage]);

    const onRowsPerPageChange = useCallback((event: any) => {
        const value = Number(event.target.value);
        setRowsPerPage(value);
        setPage(0);
    }, []);

    return {
        page,
        setPage,
        rowsPerPage,
        setRowsPerPage,
        onRowsPerPageChange,
        pageCount,
        rowsPerPageOptions,
    } as const;
}
