// Shared UI constants for Product table rows

export const EXPIRY_THRESHOLDS = {
    ALERT_DAYS: 7,
    WARN_DAYS: 15,
} as const;

export const ROW_COLORS = {
    EXPIRY_ALERT: "#F6D4D2",
    EXPIRY_WARN: "#FFFFBF",
    EXPIRY_OK: "#E4FAE4",
    STOCK_SAFE: "white",
    STOCK_WARN: "#FFB343",
    STOCK_LOW: "#ffb09c",
} as const;

export const STOCK_THRESHOLDS = {
    SAFE_MIN: 11, // > 10 is safe
    WARN_MIN: 5,  // 5-10 is warn
} as const;
