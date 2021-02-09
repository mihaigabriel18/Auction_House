package database.products;

import auction.storage.furniture.FurnitureBuilder;
import auction.storage.jewelery.JeweleryBuilder;
import auction.storage.painting.PaintingBuilder;
import auction.storage.Product;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * will read brokers from the json file
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ParseProducts {

    /**
     * json file name
     */
    String filename;

    public ParseProducts(String filename) {
        this.filename = filename;
    }

    /**
     * Gets a list of products from the json file
     * @return list of {@link Product}
     */
    public List<Product> readProducts() {
        List<Product> products = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(filename)) {

            Object obj = jsonParser.parse(reader);

            JSONArray productTypes = (JSONArray) obj;

            products.addAll(parsePaintings((JSONObject) productTypes.get(0))); // add paintings
            products.addAll(parseJewelery((JSONObject) productTypes.get(1))); // add jewelery
            products.addAll(parseFurniture((JSONObject) productTypes.get(2))); // add furniture

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return products;
    }

    private List<Product> parsePaintings(JSONObject paintings) {
        List<Product> paintingsArrayList = new ArrayList<>();
        JSONArray paintingJSONList = (JSONArray) paintings.get("painting");

        for (Object o : paintingJSONList) {
            JSONObject painting = (JSONObject) o;
            paintingsArrayList.add((new PaintingBuilder())
                    .withName((String) painting.get("name"))
                    .withId(Integer.parseInt((String) painting.get("id")))
                    .withMinimumPrice(Double.parseDouble((String) painting.get("minimum_price")))
                    .withYear(Integer.parseInt((String) painting.get("year")))
                    .withPainterName((String) painting.get("painter_name"))
                    .withColor((String) painting.get("color"))
                    .build());
        }

        return paintingsArrayList;
    }

    private List<Product> parseJewelery(JSONObject jeweleries) {
        List<Product> jeweleriesArrayList = new ArrayList<>();
        JSONArray jeweleryJSONList = (JSONArray) jeweleries.get("jewelery");

        for (Object o : jeweleryJSONList) {
            JSONObject jewelery = (JSONObject) o;
            jeweleriesArrayList.add((new JeweleryBuilder())
                    .withName((String) jewelery.get("name"))
                    .withId(Integer.parseInt((String) jewelery.get("id")))
                    .withMinimumPrice(Double.parseDouble((String) jewelery.get("minimum_price")))
                    .withYear(Integer.parseInt((String) jewelery.get("year")))
                    .withMaterial((String) jewelery.get("material"))
                    .withValuableGem(Boolean.parseBoolean
                            ((String) jewelery.get("valuable_gem")))
                    .build());
        }
        return jeweleriesArrayList;
    }

    private List<Product> parseFurniture(JSONObject furniture) {
        List<Product> furnitureArrayList = new ArrayList<>();
        JSONArray furnitureJSONList = (JSONArray) furniture.get("furniture");

        for (Object o : furnitureJSONList) {
            JSONObject jFurniture = (JSONObject) o;
            furnitureArrayList.add((new FurnitureBuilder())
                    .withName((String) jFurniture.get("name"))
                    .withId(Integer.parseInt((String) jFurniture.get("id")))
                    .withMinimumPrice(Double.parseDouble((String) jFurniture.get("minimum_price")))
                    .withYear(Integer.parseInt((String) jFurniture.get("year")))
                    .withMaterial((String) jFurniture.get("material"))
                    .withType((String) jFurniture.get("type"))
                    .build());
        }
        return furnitureArrayList;
    }

}
