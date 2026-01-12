package store;

import model.Buyer;
import model.CartItem;
import model.Product;
//import model.SaleResult;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;



public class StoreInventory {

    private final Map<Integer, Integer> stockByProductId = new LinkedHashMap<>();

    public StoreInventory(Map<Integer, Product> catalogById, Map<Integer, Integer> initialStock) {
        Objects.requireNonNull(initialStock, "initialStock");
        for (Map.Entry<Integer, Integer> e : initialStock.entrySet()) {
            int productId = e.getKey();
            int qty = e.getValue();
            if (productId <= 0) throw new IllegalArgumentException("Invalid productId in stock: " + productId);
            if (qty < 0) throw new IllegalArgumentException("Stock quantity cannot be negative for productId=" + productId);
            stockByProductId.put(productId, qty);
        }
    }

    public synchronized Map<Integer, Integer> snapshotStock() {
        return new LinkedHashMap<>(stockByProductId);
    }


}

