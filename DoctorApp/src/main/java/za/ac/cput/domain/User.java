package za.ac.cput.domain;

public class User {
        private String userId;
        private String userName;
        private String email;
        private int phoneNumber;
        private Address address;

        // Private constructor to enforce the use of Builder
        private User(UserBuilder builder) {
            this.userId = builder.userId;
            this.userName = builder.userName;
            this.email = builder.email;
            this.phoneNumber = builder.phoneNumber;
            this.address = builder.address;
        }

        // Getters
        public String getUserId() {
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

        public Address getAddress() {
            return address;
        }

        // Static nested class for Address
        public static class Address {
            private String street;
            private String city;
            private String zipCode;

            public Address(String street, String city, String zipCode) {
                this.street = street;
                this.city = city;
                this.zipCode = zipCode;
            }

            public String getStreet() {
                return street;
            }

            public String getCity() {
                return city;
            }

            public String getZipCode() {
                return zipCode;
            }

            @Override
            public String toString() {
                return street + ", " + city + ", " + zipCode;
            }
        }

        // Builder class for User
        public static class UserBuilder {
            private String userId;
            private String userName;
            private String email;
            private int phoneNumber;
            private Address address; // Optional field

            public UserBuilder(String userId, String userName) { // Required fields
                this.userId = userId;
                this.userName = userName;
            }

            public UserBuilder setEmail(String email) {
                this.email = email;
                return this;
            }

            public UserBuilder setPhoneNumber(int phoneNumber) {
                this.phoneNumber = phoneNumber;
                return this;
            }

            public UserBuilder setAddress(Address address) {
                this.address = address;
                return this;
            }

            public User build() {
                return new User(this);
            }
        }

        @Override
        public String toString() {
            return "User{" +
                    "userId='" + userId + '\'' +
                    ", userName='" + userName + '\'' +
                    ", email='" + email + '\'' +
                    ", phoneNumber=" + phoneNumber +
                    ", address=" + (address != null ? address.toString() : "N/A") +
                    '}';
        }
    }



