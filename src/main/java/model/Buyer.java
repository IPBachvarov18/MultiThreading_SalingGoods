package model;

import java.util.List;

public class Buyer {
    int id;
    String name;
    List<CartItem> cartItemList;

    public Buyer(int id, List<CartItem> cartItemList, String name) {
        this.id = id;
        this.cartItemList = cartItemList;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CartItem> getCartItemList() {
        return cartItemList;
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }
}
