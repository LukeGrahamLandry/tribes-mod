package io.github.lukegrahamlandry.tribes.tribe_data;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DeitiesManager {
    public static class DeityData {
        public final String key;
        public final String displayName;
        public final String bookTitle;
        public final String label;
        public final String bookAuthor;
        public final List<String> domains;
        public final boolean enabled;
        public List<String> bookPages = new ArrayList<>();

        public DeityData(String key, String displayName, String bookTitle, String bookAuthor, String label, List<String> domains, boolean enabled){
            this.key = key;
            this.displayName = displayName;
            this.bookTitle = bookTitle;
            this.bookAuthor = bookAuthor;
            this.label = label;  // god / goddess
            this.domains = domains;
            this.enabled = enabled;
        }

        public void generateBook(String rawBookContent) {
            this.bookPages.clear();
        }
    }

    public static HashMap<String, DeityData> deities = new HashMap();

    public static void readFromString(String data){
        deities.clear();

        JsonArray allDeitiesJson = new JsonParser().parse(data).getAsJsonArray();
        for (JsonElement e : allDeitiesJson){
            JsonObject deityJson = e.getAsJsonObject();
            String key = deityJson.get("key").getAsString();
            String displayName = deityJson.get("display name").getAsString();
            String bookTitle = deityJson.get("book title").getAsString();
            String label = deityJson.get("label").getAsString();
            String bookAuthor = deityJson.get("book author").getAsString();
            List<String> domains = new ArrayList<>();
            deityJson.get("domains").getAsJsonArray().forEach((d) -> domains.add(d.getAsString()));
            boolean enabled = deityJson.get("enabled").getAsBoolean();

            DeityData result = new DeityData(key, displayName, bookTitle, bookAuthor, label, domains, enabled);
            deities.put(key, result);
        }
    }

    public static final DeityData EXAMPLE_DEITY = new DeityData("example", "Examplar", "The Example Text", "The Tribes Dev", "God", Arrays.asList("Examples", "Mistakes", "Knowledge"), false);
    public static String generateExampleJson(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonArray allDeitiesJson = new JsonArray();
        JsonObject obj = new JsonObject();
        obj.addProperty("key", EXAMPLE_DEITY.key);
        obj.addProperty("display name", EXAMPLE_DEITY.displayName);
        obj.addProperty("book title", EXAMPLE_DEITY.bookTitle);
        obj.addProperty("book author", EXAMPLE_DEITY.bookAuthor);
        obj.addProperty("label", EXAMPLE_DEITY.label);
        obj.addProperty("enabled", EXAMPLE_DEITY.enabled);
        JsonArray domainsJson = new JsonArray();
        EXAMPLE_DEITY.domains.forEach(domainsJson::add);
        obj.add("domains", domainsJson);
        allDeitiesJson.add(obj);
        return gson.toJson(allDeitiesJson);
    }
}
