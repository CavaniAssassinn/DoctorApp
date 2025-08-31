/* UserFactory. java
Author: Nompumelelo Bhebhe(221584455)
Date: March 2025 */

package za.ac.cput.domain;

public class User {
    private final int userId;
    private final String userName;
    private final String email;
    private final int phoneNumber;

    private User(Builder builder) {
        this.userId = builder.userId;
        this.userName = builder.userName;
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public static class Builder {
        private int userId;
        private String userName;
        private String email;
        private int phoneNumber;

        public Builder setUserId(int userId) {
            this.userId = userId;
            return this;
        }

        public Builder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPhoneNumber(int phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
