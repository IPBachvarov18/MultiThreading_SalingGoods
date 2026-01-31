package org.example;

import config.ExecutorServiceConfig;
import model.Buyer;
import model.CartItem;
import model.Product;
import model.SaleResult;
import store.StoreInventory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class Main {
    public static void main(String[] args) {
        Map<Integer, Product> catalog = new LinkedHashMap<>();
        catalog.put(1, new Product(1, "apple"));
        catalog.put(2, new Product(2, "peach"));


        Map<Integer, Integer> initialStock = new LinkedHashMap<>();
        initialStock.put(1, 12);
        initialStock.put(2, 6);

        StoreInventory store = new StoreInventory(catalog, initialStock);


        List<Buyer> buyers = new ArrayList<>(List.of(
                new Buyer(1, "Ivan", new ArrayList<>(List.of(new CartItem(1, 2), new CartItem(2, 1)))),
                new Buyer(2, "Andrei", new ArrayList<>(List.of(new CartItem(1, 5)))),
                new Buyer(3, "Georgi", new ArrayList<>(List.of(new CartItem(2, 3)))),
                new Buyer(4, "Dimitar", new ArrayList<>(List.of(new CartItem(1, 3), new CartItem(2, 2)))),
                new Buyer(5, "Ivana", new ArrayList<>(List.of(new CartItem(1, 10)))),
                new Buyer(6, "Gloriq", new ArrayList<>(List.of(new CartItem(1, 1)))),
                new Buyer(7, "Vasil", new ArrayList<>(List.of(new CartItem(2, 5)))),
                new Buyer(8, "Nasko", new ArrayList<>(List.of(new CartItem(1, 1)))),
                new Buyer(9, "Andrea", new ArrayList<>(List.of(new CartItem(1, 1)))),
                new Buyer(10, "Raina", new ArrayList<>(List.of(new CartItem(2, 1))))
        ));

        ExecutorService executor = ExecutorServiceConfig.getExecutorService();

        List<CompletableFuture<SaleResult>> futures = new ArrayList<>();

        for (Buyer buyer : buyers) {
            CompletableFuture<SaleResult> f = CompletableFuture.supplyAsync(()->{
                SaleResult sr = null;
                sr = store.sellAllOrNothing(buyer);

                return sr;
            }, executor);

            futures.add(f);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        executor.shutdown();


    }
}
