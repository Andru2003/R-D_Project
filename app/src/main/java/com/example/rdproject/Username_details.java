package com.example.rdproject;

public class Username_details {

   private String email, username, password, description, image;

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public String geImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Username_details(String email, String username, String password, String description, String image){
       this.email = email;
       this.username = username;
       this. password = password;
       this.description = description;
       this.image = image;
   }

}
