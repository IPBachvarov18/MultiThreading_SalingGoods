package model;

import java.util.Objects;

public class SaleResult {
    private final int buyerId;
    private final boolean success;
    private final String message;

    private SaleResult(int buyerId, boolean success, String message) {
        this.buyerId = buyerId;
        this.success = success;
        this.message = message;
    }

    public static SaleResult success(int buyerId, String message) {
        return new SaleResult(buyerId, true, Objects.requireNonNull(message, "message"));
    }

    public static SaleResult failure(int buyerId, String message) {
        return new SaleResult(buyerId, false, Objects.requireNonNull(message, "message"));
    }

    public int getBuyerId() { return buyerId; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return "SaleResult{buyerId=" + buyerId + ", success=" + success + ", message='" + message + "'}";
    }
}
