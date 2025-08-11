type HttpMethod = "GET" | "POST" | "PUT" | "DELETE";

export const BASE_URL = process.env.REACT_APP_API_URL || "http://localhost:9090";

export class ApiError extends Error {
    status: number;
    body?: unknown;
    constructor(message: string, status: number, body?: unknown) {
        super(message);
        this.status = status;
        this.body = body;
    }
}

async function parseJsonSafe(res: Response) {
    const text = await res.text();
    try {
        return text ? JSON.parse(text) : undefined;
    } catch {
        return text; // non-json
    }
}

export async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
    const url = path.startsWith("http") ? path : `${BASE_URL}${path}`;
    const res = await fetch(url, {
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {}),
        },
        ...options,
    });

    const data = await parseJsonSafe(res);

    if (!res.ok) {
        const message = (data as any)?.message || `HTTP ${res.status}`;
        throw new ApiError(message, res.status, data);
    }
    return data as T;
}

export function get<T>(path: string, params?: Record<string, any>) {
    let full = path;
    if (params && Object.keys(params).length) {
        const qs = new URLSearchParams(params as any).toString();
        full += `?${qs}`;
    }
    return request<T>(full, { method: "GET" });
}

export function post<T>(path: string, body?: unknown) {
    return request<T>(path, { method: "POST", body: body != null ? JSON.stringify(body) : undefined });
}

export function put<T>(path: string, body?: unknown) {
    return request<T>(path, { method: "PUT", body: body != null ? JSON.stringify(body) : undefined });
}

export function del<T = void>(path: string) {
    return request<T>(path, { method: "DELETE" });
}
