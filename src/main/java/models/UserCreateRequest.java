package models;

public class UserCreateRequest {
    private String email;
    private String password;
    private String name;

    public UserCreateRequest(String email, String password, String name) {
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public UserCreateRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
