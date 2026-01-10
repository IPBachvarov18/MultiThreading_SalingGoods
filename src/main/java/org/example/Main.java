package org.example;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Map<String, Integer> initialInventory = new LinkedHashMap<>();
        initialInventory.put("Bread", 20);
        initialInventory.put("Milk", 25);
        initialInventory.put("Eggs", 40);
        initialInventory.put("Cheese", 15);
        initialInventory.put("Apples", 50);
        initialInventory.put("Tomatoes", 35);

        Store store = new Store(initialInventory);

        List<Customer> customers = List.of(
                new Customer("Customer-1", basket("Bread", 4, "Milk", 2, "Apples", 6), store),
                new Customer("Customer-2", basket("Bread", 5, "Cheese", 3, "Tomatoes", 4), store),
                new Customer("Customer-3", basket("Milk", 5, "Eggs", 12), store),
                new Customer("Customer-4", basket("Eggs", 10, "Apples", 10, "Tomatoes", 5), store),
                new Customer("Customer-5", basket("Cheese", 5, "Bread", 3), store),
                new Customer("Customer-6", basket("Milk", 6, "Apples", 8), store),
                new Customer("Customer-7", basket("Eggs", 15, "Tomatoes", 7), store),
                new Customer("Customer-8", basket("Bread", 6, "Milk", 4, "Cheese", 4), store),
                new Customer("Customer-9", basket("Apples", 12, "Tomatoes", 10), store),
                new Customer("Customer-10", basket("Eggs", 8, "Cheese", 6, "Bread", 7), store)
        );

        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<?>> futures = new ArrayList<>();

        for (Customer customer : customers) {
            futures.add(executor.submit(customer));
        }

        for (Future<?> future : futures) {
            future.get();
        }

        executor.shutdown();

        System.out.println("Final inventory:");
        for (Map.Entry<String, Integer> entry : store.snapshot().entrySet()) {
            System.out.printf("- %s: %d%n", entry.getKey(), entry.getValue());
        }
    }

    private static Map<String, Integer> basket(Object... items) {
        Map<String, Integer> basket = new LinkedHashMap<>();
        for (int i = 0; i < items.length; i += 2) {
            basket.put((String) items[i], (Integer) items[i + 1]);
        }
        return basket;
    }

    private static final class Store {
        private final Map<String, Integer> inventory;

        private Store(Map<String, Integer> initialInventory) {
            this.inventory = new LinkedHashMap<>(initialInventory);
        }

        public synchronized int sell(String product, int requested) {
            int available = inventory.getOrDefault(product, 0);
            int sold = Math.min(available, requested);
            if (sold > 0) {
                inventory.put(product, available - sold);
            }
            return sold;
        }

        public Map<String, Integer> snapshot() {
            synchronized (this) {
                return new LinkedHashMap<>(inventory);
            }
        }
    }

    private static final class Customer implements Runnable {
        private final String name;
        private final Map<String, Integer> basket;
        private final Store store;

        private Customer(String name, Map<String, Integer> basket, Store store) {
            this.name = name;
            this.basket = basket;
            this.store = store;
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            for (Map.Entry<String, Integer> entry : basket.entrySet()) {
                String product = entry.getKey();
                int requested = entry.getValue();
                int sold = store.sell(product, requested);
                if (sold == requested) {
                    System.out.printf("[%s] %s bought %d of %s%n", threadName, name, sold, product);
                } else {
                    System.out.printf("[%s] %s wanted %d of %s, sold %d%n", threadName, name, requested, product, sold);
                }
            }
        }
    }
}
