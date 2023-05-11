package com.goit.javacore.m13;
import com.goit.javacore.m13.entities.*;
//import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class JsonPlaceholderAPITest {
    private static final String API_BASE_URL = "https://jsonplaceholder.typicode.com";
    private final HttpClient httpClient;
    private final Gson gson;
    private Gson gsonPretty;

    public JsonPlaceholderAPITest() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.gsonPretty = new GsonBuilder().setPrettyPrinting().create();
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
    // get user by id
    public User getUserById(int userId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/users/" + userId))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), User.class);
        } else if (response.statusCode() == 404) {
            return null; // User not found
        } else {
            throw new RuntimeException("Failed to get user: " + response.body());
        }
    }

    public User getUserByUserName(String username) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/users?username="+username))
                .build();
        //System.out.println(request.uri());

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {

            User[] userByUserName = gson.fromJson(response.body(), User[].class);
            if (userByUserName.length > 0) {
                return userByUserName[0];
            } else {
                return null;
            }

        } else {
            throw new RuntimeException("Failed to get users: " + response.body());
        }
    }


    public void getCommentsForLastPostByUser(int userId) throws IOException, InterruptedException {

        // First, get all posts for this user

        HttpRequest postsRequest = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/users/" + userId + "/posts"))
                .build();
        HttpResponse<String> postsResponse = httpClient.send(postsRequest, HttpResponse.BodyHandlers.ofString());



        if (postsResponse.statusCode() >= 200 && postsResponse.statusCode() < 300) {
            Post[] posts = gson.fromJson(postsResponse.body(), Post[].class);
            if (posts.length == 0) {
                System.out.println("User " + userId + " has no posts.");
                return;
            }

            // Sort posts by id in descending order and get the last one
            Arrays.sort(posts, Comparator.comparing(Post::getId).reversed());
            Post lastPost = posts[0];

            // Get all comments for the last post
            HttpRequest commentsRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/posts/" + lastPost.getId() + "/comments"))
                    .build();

            HttpResponse<String> commentsResponse = httpClient.send(commentsRequest, HttpResponse.BodyHandlers.ofString());

            if (commentsResponse.statusCode() >= 200 && commentsResponse.statusCode() < 300) {
                Comment[] comments = gson.fromJson(commentsResponse.body(), Comment[].class);
                // Write comments to file
                String fileName = "user-" + userId + "-post-" + lastPost.getId() + "-comments.json";
                fileName = "src\\main\\resources\\"+fileName;
                try (FileWriter writer = new FileWriter(fileName)) {
                    gsonPretty.toJson(comments, writer);
                }
                System.out.println("Comments for last post by user " + userId + " written to file " + fileName);
            } else {
                throw new RuntimeException("Failed to get comments: " + commentsResponse.body());
            }
        } else {
            throw new RuntimeException("Failed to get posts: " + postsResponse.body());
        }
    }

    public List<Task> getOpenTasksForUser(int userId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/users/" + userId + "/todos"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            Task[] tasks = gson.fromJson(response.body(), Task[].class);

            // Write comments to file
            String fileName = "user-" + userId +"-opentasks.json";
            fileName = "src\\main\\resources\\"+fileName;
            try (FileWriter writer = new FileWriter(fileName)) {
                gsonPretty.toJson(tasks, writer);
            }
            System.out.println("Tasks for user " + userId + " written to file " + fileName);


            return Arrays.stream(tasks)
                    .filter(task -> !task.isCompleted())
                    .collect(Collectors.toList());
        } else {
            throw new RuntimeException("Failed to get tasks: " + response.body());
        }
    }





    /**
     * fill User form file newUser.json
     * @return
     * @throws IOException
     */
    public User getUserFromFile() throws IOException {

        StringBuffer jsonStringBuffer = new StringBuffer();
        String str1;
        try (BufferedReader br = new BufferedReader(
                new FileReader("src\\main\\resources\\newUser.json"))){
            while((str1=br.readLine())!=null){
              jsonStringBuffer.append(str1);
              }
            return new Gson().fromJson(jsonStringBuffer.toString(), User.class);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return null;
    }


}

