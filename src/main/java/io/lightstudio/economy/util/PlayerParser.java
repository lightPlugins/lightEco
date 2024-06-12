package io.lightstudio.economy.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.lightstudio.economy.Light;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerParser {

    public static CompletableFuture<UUID> getPlayerUUID(String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Light.getConsolePrinting().debug("Getting UUID for player " + playerName + " from Mojang API");
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                Light.getConsolePrinting().debug("Response code: " + connection.getResponseCode());

                if (connection.getResponseCode() == 200) {
                    try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                        JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
                        return UUID.fromString(object.get("id").getAsString()
                                .replaceFirst(
                                        "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                                        "$1-$2-$3-$4-$5"));
                    }
                } else {
                    try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                        JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
                        Light.getConsolePrinting().debug("Error message from Mojang: " + object.get("errorMessage").getAsString());
                    }
                    throw new RuntimeException("Could not get UUID for player " + playerName);
                }
            } catch (IOException e) {
                throw new RuntimeException("Could not get UUID for player " + playerName, e);
            }
        });
    }
}
