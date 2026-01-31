package store;

import model.Buyer;
import model.CartItem;
import model.Product;
import model.SaleResult;
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
    public synchronized SaleResult sellAllOrNothing(Buyer buyer) {
        System.out.println(Thread.currentThread() + " -> " + buyer.getName() + " iska da kupi: " + cartToHuman(buyer));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return SaleResult.failure(buyer.getId(), "Prekasnato (interrupt).");
        }

        for (CartItem ci : buyer.getCartItemList()) {
            int productId = ci.getProductId();
            int qty = ci.getQuantity();
            int availableQty = stockByProductId.getOrDefault(productId, 0);

            if (availableQty < qty) {
                System.out.println(Thread.currentThread() + " -> " + buyer.getName() + " e izgonen (nqma nalichnost).");
                System.out.println(Thread.currentThread() + " -> Sklad (ostava): " + snapshotStock());
                return SaleResult.failure(buyer.getId(), "Nema biznes, nema pari");
            }
        }

        System.out.println(Thread.currentThread() + " -> Sklad predi commit: " + snapshotStock());

        for (CartItem item : buyer.getCartItemList()) {
            int productId = item.getProductId();
            int qty = item.getQuantity();
            int before = stockByProductId.getOrDefault(productId, 0);
            int after = before - qty;
            stockByProductId.put(productId, after);
        }

        System.out.println(Thread.currentThread() + " -> Sklad sled commit: " + snapshotStock());
        System.out.println(Thread.currentThread() + " -> " + buyer.getName() + " e gotov kupil.");

        return SaleResult.success(buyer.getId(), "Uspeshna pokupka za " + buyer.getName() + ".");
    }
    private String cartToHuman(Buyer buyer) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < buyer.getCartItemList().size(); i++) {
            CartItem ci = buyer.getCartItemList().get(i);
            sb.append("productId=").append(ci.getProductId()).append(" x").append(ci.getQuantity());
            if (i < buyer.getCartItemList().size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }


}

