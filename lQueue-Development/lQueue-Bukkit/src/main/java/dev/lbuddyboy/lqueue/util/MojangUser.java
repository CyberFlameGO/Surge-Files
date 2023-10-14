package dev.lbuddyboy.lqueue.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

@Data
public class MojangUser {

    private String name;
    private UUID uuid;
    private boolean valid;

    public MojangUser(String name) throws IOException {
        fromUrl("https://api.mojang.com/users/profiles/minecraft/" + name);
    }

    public MojangUser(UUID uuid) throws IOException {
        fromUrl("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", ""));
    }

    private void fromUrl(String s) throws IOException {
        URL url = new URL(s);
        HttpsURLConnection c = (HttpsURLConnection) url.openConnection();
        c.setRequestMethod("GET");
        c.setHostnameVerifier((hostname, session) -> true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));

        StringBuilder stringBuilder = new StringBuilder();
        int cp;
        while ((cp = reader.read()) != -1) {
            stringBuilder.append((char) cp);
        }

        String jsonString = stringBuilder.toString();
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        if (json != null) {
            this.uuid = UUID.fromString(json.get("id").getAsString().replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                    "$1-$2-$3-$4-$5"
            ));
            this.name = json.get("name").getAsString();
            this.valid = true;
        } else {
            this.name = null;
            this.uuid = null;
            this.valid = false;
        }
    }

}
