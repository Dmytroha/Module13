package com.goit.javacore.m13;
import com.goit.javacore.m13.entities.*;
//import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

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
            return null;
            //throw new RuntimeException("Failed to create user: " + response.body());
        }
    }

    public User updateUser(User user) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/users/" + user.getId()))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(user)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {

            // User updated successfully
            return user;
        } else {
            return null;
            //throw new RuntimeException("Failed to update user: " + response.body());
        }
    }

    public int deleteUser(int userId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/users/" + userId))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();

    }

    public List<User> getAllUsers() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/users"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            User[] users = gson.fromJson(response.body(), User[].class);
            return Arrays.asList(users);
        } else {
            throw new RuntimeException("Failed to get users: " + response.body());
        }
    }

    /**
     * fill User form file newUser.json
     * @return
     * @throws IOException
     */
    public User getUserFromFile() throws IOException {
        StringBuffer json = new StringBuffer();
        String str;
        try (BufferedReader br = new BufferedReader(
                new FileReader("src\\main\\resources\\newUser.json"))){
            while((str=br.readLine())!=null){
              json.append(str);
              }
            return new Gson().fromJson(json.toString(), User.class);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }


        return null;
    }



}

