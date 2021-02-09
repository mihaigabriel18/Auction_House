package command.initialize_auctionhouse;

import auction.institution.AuctionHouse;
import auction.institution.employee.Administrator;
import auction.storage.Product;
import command.Command;
import database.products.ParseProducts;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Load the products to the store {@link Command}
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LoadProducts implements Command {

    static String FILENAME = "product_deposit.json";

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        Administrator administrator = AuctionHouse.getInstance().getAdministrator();
        ParseProducts parser = new ParseProducts(FILENAME);

        List<Product> products = parser.readProducts();

        products.forEach(administrator::addProduct);
    }
}
