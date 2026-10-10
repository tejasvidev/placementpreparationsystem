package placement.model;

public abstract class User {

    private final String username;
    private final String passwordHash;
    private final String name;
    private final String email;

    public User(String username,
                String passwordHash,
                String name,
                String email) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Invalid username");
        }

        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Invalid password hash");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Invalid name");
        }

        if (email == null
                || !email.contains("@")
                || !email.contains(".")
                || email.indexOf("@") >= email.lastIndexOf(".")) {
            throw new IllegalArgumentException("Invalid email");
        }

        this.username = username;
        this.passwordHash = passwordHash;
        this.name = name;
        this.email = email;
    }

    public abstract void displayProfile();

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
    
    public String getEmail() {
        return email;
    }
}