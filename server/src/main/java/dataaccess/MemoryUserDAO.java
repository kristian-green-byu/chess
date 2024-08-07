package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    final private HashMap<Integer, UserData> users = new HashMap<>();
    private int nextID = 1;

    public UserData getUser(String username){
        for (UserData user : users.values()) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public void createUser(UserData user) {
        users.put(nextID++, user);
    }

    public void clearUserData() {
        users.clear();
        nextID = 1;
    }
}
