package org.example.demo1207.context;

import org.example.demo1207.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserContext {

    private static final ThreadLocal<User>  currentUser = new ThreadLocal<User>();

    public static User getCurrentUser() {
        return currentUser.get();
    }

    public static void setCurrentUser(User user) {
        currentUser.set(user);
    }

    public static void removeCurrentUser() {
        currentUser.remove();
    }
}
