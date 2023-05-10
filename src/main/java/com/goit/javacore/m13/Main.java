package com.goit.javacore.m13;

import com.goit.javacore.m13.entities.Address;
import com.goit.javacore.m13.entities.User;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("<-------Task #1 implementation!---------->");
        // Task 1.1 create new user at "https://jsonplaceholder.typicode.com/users"

        // create JsonPlaceholderAPITest object
        JsonPlaceholderAPITest jsonPlaceholder = new JsonPlaceholderAPITest();
        // read user from file
        User user = jsonPlaceholder.getUserFromFile();
        // create new user at "https://jsonplaceholder.typicode.com/users"
        if(user!=null) {
            System.out.println("***Create user ->>"+jsonPlaceholder.createUser(user)); //create new user
        }

        // Task 1.2 update user at "https://jsonplaceholder.typicode.com/users"
        user = jsonPlaceholder.getUserFromFile();
        if(user!=null) {
            user.setId(2); // set user id to update
            user.setName("John Door"); // change user name
            System.out.println("User before update --->> "+user); // print user before
            System.out.println("User after update ---->> " +jsonPlaceholder.updateUser(user)); //update user and print
        } else {
            System.out.println("Can`t get User form json file. Please check file.");
        }

        // delete user with id=5 from "https://jsonplaceholder.typicode.com/users"

        int status = jsonPlaceholder.deleteUser(5);
        if (status >= 200 && status < 300) {
            // User deleted successfully
            System.out.println("User deleted successfully");
        } else {
            System.out.println("Something goes wrong! User is not deleted.");
        }


        // get all users from https://jsonplaceholder.typicode.com/users
        List<User> userList = jsonPlaceholder.getAllUsers();

        System.out.println("We get " + userList.size()+" users:");
        System.out.println(userList.get(0)+"\n......\n"+ userList.get(9));

        // System.out.println(userList); //remove "//" to print all users

        // get user by Id
        int id1=3;
        System.out.println("User by Id: "+id1+" looks like---> "+jsonPlaceholder.getUserById(id1));


        // get user by username

        String userName1="Bret";
        System.out.println("User by username \""+userName1+"\" looks like---> "+jsonPlaceholder.getUserByUserName(userName1));

    }



}