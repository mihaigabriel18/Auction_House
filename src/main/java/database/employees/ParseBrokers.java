package database.employees;

import auction.institution.employee.broker.Broker;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * will read brokers from the json file
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ParseBrokers {

    /**
     * json file name
     */
    String filename;

    public ParseBrokers(String filename) {
        this.filename = filename;
    }

    /**
     * Gets a list of brokers from the json file
     * @return list of {@link Broker}
     */
    public List<Broker> readBrokers() {
        List<Broker> brokers = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(filename)) {

            Object obj = jsonParser.parse(reader);
            JSONObject productTypes = (JSONObject) obj;

            JSONArray jsonNames = (JSONArray) productTypes.get("name");
            for (Object o : jsonNames)
                brokers.add(new Broker((String) o));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return brokers;
    }
}
