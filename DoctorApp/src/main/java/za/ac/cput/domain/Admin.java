/* Admin.java
Admin model class
Author : Nothile Cele - 230894356
Date: March 2025
 */
package za.ac.cput.domain;

public class Admin {
    private final int adminID;
    private final String role;
    private final String userID;
    private final String name;
    private final String email;
    private final String phoneNumber;

    private Admin(Builder builder) {
        this.adminID = builder.adminID;
        this.role = builder.role;
        this.userID = builder.userID;
        this.name = builder.name;
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
    }

    // Getters
    public int getAdminID() { return adminID; }
    public String getRole() { return role; }
    public String getUserID() { return userID; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }

    // Builder class
    public static class Builder {
        private int adminID;
        private String role;
        private String userID = "DEFAULT_ID";      // Default value
        private String name = "DEFAULT_NAME";      // Default value
        private String email = "default@email.com"; // Default value
        private String phoneNumber = "0000000000";  // Default value

        // Builder methods
        public Builder setAdminID(int adminID) {
            this.adminID = adminID;
            return this;
        }

        public Builder setRole(String role) {
            this.role = role;
            return this;
        }

        public Builder setUserID(String userID) {
            this.userID = userID;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Admin build() {
            return new Admin(this);
        }
    }
}