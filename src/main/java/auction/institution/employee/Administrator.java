package auction.institution.employee;

import auction.institution.AuctionHouse;
import auction.storage.Product;
import gui.ProductDeposit;
import gui.StartWindow;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

/**
 * The administrator will be able to add new products to the {@link AuctionHouse}'s product list
 * Products cannot be added without an administrator.
 * The administrator notifies the GUI panel that shows the products and makes it update the product panel
 * every time a new one is added (this is even simulated with a {@code Thread.sleep()} call, making
 * the user think that the products are manually grabbed from the deposit to the actual store)
 * The addition of products is done with the producer-consumer design pattern, the administrator
 * being the producer.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Administrator {

    @Setter
    @Getter
    String name;

    /**
     * This is a flag for the multithreading part, another thread will instantiate this product,
     * signaling this thread that it can continue its job
     */
    Object flagWait;

    /**
     * Delay between adding products, used only for simulation
     */
    static final int DELAY = 200;

    public Administrator(String name) {
        this.flagWait = null;
        this.name = name;
    }

    /**
     * Before calling {@code notifyAll()} this method needs to be called
     */
    public void setFlagWait() {
        this.flagWait = new Object();
    }

    /**
     * Adds a product to the list of products from {@link AuctionHouse} and also updates
     * the GUI panel {@link ProductDeposit} with the new product
     * @param product the product to add
     */
    @SneakyThrows
    public synchronized void addProduct(Product product) {
        ProductDeposit productDeposit = StartWindow.getInstance().getProductDeposit();
        addProductToDeposit(product);

        synchronized (productDeposit) {
            productDeposit.setProductToAdd(product);
            productDeposit.notifyAll();
        }

        while (flagWait == null) // while the GUI panel doesnt instantiate the flag
            this.wait();

        // reset flag
        flagWait = null;

        // simulate the addition of products
        Thread.sleep(DELAY);
    }

    /**
     * Adds a product to the list of products from {@link AuctionHouse}
     * @param product the product to add
     */
    public void addProductToDeposit(Product product) {
        AuctionHouse auctionHouse = AuctionHouse.getInstance();
        auctionHouse.lockProductList();

        auctionHouse.addNewProduct(product);

        auctionHouse.unlockProductList();
    }

}
