package za.ac.cput.domain;

public class Admin {
    private final int adminID;
    private final String role;

    // Private constructor
    private Admin(Builder builder) {
        this.adminID = builder.adminID;
        this.role = builder.role;
    }

    // Getters
    public int getAdminID() { return adminID; }
    public String getRole() { return role; }

    // Builder class
    public static class Builder {
        private int adminID;
        private String role;

        public Builder setAdminID(int adminID) {
            this.adminID = adminID;
            return this;
        }

        public Builder setRole(String role) {
            this.role = role;
            return this;
        }

        public Admin build() {
            return new Admin(this);
        }
    }
}