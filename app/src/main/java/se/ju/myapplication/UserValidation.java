package se.ju.myapplication;

import org.jetbrains.annotations.NotNull;

class UserValidation {

    class Credentials {
        private final String username;
        private final String password;

        public Credentials(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    private static final UserValidation ourInstance = new UserValidation();

    private Boolean isSignedIn = false;
    private String token = "";

    @org.jetbrains.annotations.Contract(pure = true)
    public static UserValidation getInstance() {
        return ourInstance;
    }

    private UserValidation() {
        // TODO do request and check if existing credentials token exists. Default to false

    }

    public void login(@NotNull Credentials credentials) {
        // TODO do request with credentials.
        if (credentials.username.equals("bob") && credentials.password.equals("abc")) { // Testing!
            this.token = "123987";
            this.isSignedIn = true;
        }
    }

    public Boolean getSignedIn() {
        return isSignedIn;
    }
}

