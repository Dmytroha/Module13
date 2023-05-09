package com.goit.javacore.m13;
import com.goit.javacore.m13.entities.*;
//import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JsonPlaceholderAPITest {
    private static final String API_BASE_URL = "https://jsonplaceholder.typicode.com";
    private final HttpClient httpClient;
    private final Gson gson;

    public JsonPlaceholderAPITest() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }
    public User createUser(User user) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(user)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);
            int newUserId = jsonObject.get("id").getAsInt();
            user.setId(newUserId);
            return user;
        } else {
            throw new RuntimeException("Failed to create user: " + response.body());
        }
    }

}

