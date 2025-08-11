export const required = (value: unknown) => (value === undefined || value === null || value === "" ? "This field is required" : "");

export const nonNegative = (value: unknown) => {
    const num = Number(value);
    if (value === "" || Number.isNaN(num) || num < 0) return "Must be a number greater than or equal to 0";
    return "";
};
