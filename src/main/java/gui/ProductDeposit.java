package gui;

import auction.institution.AuctionHouse;
import auction.storage.furniture.Furniture;
import auction.storage.jewelery.Jewelery;
import auction.storage.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import javax.swing.*;
import java.awt.*;

/**
 * A panel that will hold a list with the {@link Product}s available to sell
 * updated at the beginning and after each time a product will be sold
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductDeposit implements Runnable {

    @Getter
    JPanel panel;

    /**
     * Product that will be added to the list, update each time the administrator adds a new product
     * The administrator will update the value and the run method will add it to the list
     */
    @Setter @NonFinal
    Product productToAdd;

    @Getter
    private final DefaultListModel<String> model;

    /**
     * initialize all swing components
     */
    public ProductDeposit() {
        this.productToAdd = null;
        this.panel = new JPanel();
        this.panel.setBounds(300, 10, 500, 550);
        this.panel.setBackground(Color.GRAY);
        this.panel.setLayout(null);

         // list with products
        JList<String> products = new JList<>();
        model = new DefaultListModel<>();
        products.setBounds(0, 0, 500, 550);
        products.setBackground(Color.WHITE);
        products.setModel(model);

        panel.add(products, "Center");
    }

    /**
     * will receive a product and log it to the list
     * @param product product to log for
     * @return the log for this product
     */
    public String createLogForProduct(Product product) {
        String type = "";
        if (product instanceof Furniture)
            type += "Furniture";
        else if (product instanceof Jewelery)
            type += "Jewelery";
        else // instance of painting
            type += "Painting";

        return type + " named: " + product.getName() + ", minimum price: " + product.getMinimumPrice() +
                " and id: " + product.getId();

    }

    /**
     * Will wait for the main thread to add new products and update the list with that product
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public synchronized void run() {
        while (Thread.currentThread().isAlive()) {

            this.wait();
            synchronized (AuctionHouse.getInstance().getAdministrator()) {

                model.addElement(createLogForProduct(productToAdd));

                AuctionHouse.getInstance().getAdministrator().setFlagWait();
                AuctionHouse.getInstance().getAdministrator().notifyAll();
            }

        }
    }

    /**
     * Remove a product form the list of products, called after a product is sold
     * @param product product to sell
     * @throws IllegalArgumentException if the product is not in the list
     */
    public void removeProduct(Product product) {
        int index = -1;
        for (int i = 0; i < model.size(); i++) {
            if (product.getId().toString().equals(model.get(i).split("id: ")[1])) {
                index = i;
                break;
            }
        }
        if (index < 0)
            throw new IllegalArgumentException("Product is not the list, this should not have happened");
        model.remove(index);
    }
}
