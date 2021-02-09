package database.employees;

import auction.institution.clients.Client;
import auction.institution.clients.JuridicalPerson;
import auction.institution.clients.PhysicalPerson;
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
public class ParseClients {

    /**
     * json file name
     */
    String filename;

    public ParseClients(String filename) {
        this.filename = filename;
    }

    /**
     * Gets a list of clients from the json file
     * @return list of {@link Client}
     */
    public List<Client> readClients() {
        List<Client> clients = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(filename)) {

            Object obj = jsonParser.parse(reader);

            JSONArray clientTypes = (JSONArray) obj;

            clients.addAll(parsePhysicalClient((JSONObject) clientTypes.get(0)));
            clients.addAll(parseJuridicalClient((JSONObject) clientTypes.get(1)));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return clients;
    }

    private List<Client> parsePhysicalClient(JSONObject pClients) {
        List<Client> pClientList = new ArrayList<>();
        JSONArray pClientJSONList = (JSONArray) pClients.get("physical");

        for (Object o : pClientJSONList) {
            JSONObject client = (JSONObject) o;
            pClientList.add(new PhysicalPerson(
                    Integer.parseInt((String) client.get("id")),
                    (String) client.get("name"),
                    (String) client.get("address"),
                    (String) client.get("birthday")));
        }
        return pClientList;
    }

    private List<Client> parseJuridicalClient(JSONObject jClients) {
        List<Client> jClientList = new ArrayList<>();
        JSONArray jClientJSONList = (JSONArray) jClients.get("juridical");

        for (Object o : jClientJSONList) {
            JSONObject client = (JSONObject) o;
            jClientList.add(new JuridicalPerson(
                    Integer.parseInt((String) client.get("id")),
                    (String) client.get("name"),
                    (String) client.get("address"),
                    (String) client.get("companyType"),
                    Double.parseDouble((String) client.get("socialCapital"))));
        }
        return jClientList;
    }
}
