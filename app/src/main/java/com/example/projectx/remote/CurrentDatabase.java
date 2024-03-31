package com.example.projectx.remote;


import com.example.projectx.remote.models.User;

/**
 * The type Current database.
 */
public class CurrentDatabase {

    private static User currentUser;

    /**
     * Gets current public user.
     *
     * @return the current public user
     */
    public static User getCurrentPublicUser() {
        return currentUser;
    }

    /**
     * Sets current public user.
     *
     * @param user the user
     */
    public static void setCurrentPublicUser(User user) {
        CurrentDatabase.currentUser = user;
    }

}