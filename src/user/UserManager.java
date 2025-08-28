package user;

import exception.*;
import tradable.*;

import java.util.TreeMap;

public class UserManager {
    private TreeMap<String, User> users = new TreeMap<>();
    private static UserManager instance;

    public void init(String[] usersIn) throws DataValidationException{
        if (usersIn.length == 0){
            throw new DataValidationException("Users can't be empty");
        }
        for (String user : usersIn) {
            User newUser = new User(user);
            users.put(user, newUser);
        }
    }

    public void updateTradable(String userId, TradableDTO o) throws DataValidationException{
        if (userId == null) {
            throw new DataValidationException("Null User ID");
        }
        if (o == null) {
            throw new DataValidationException("Null Tradable Object");
        }
        User user = users.get(userId);
        if (user == null) {
            throw new DataValidationException("User not found");
        }

        user.updateTradables(o);

    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public User getUser(String userId) throws DataValidationException {
        if (userId == null) {
            throw new DataValidationException("Null User ID");
        }
        return users.get(userId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (User user : users.values()) {
            sb.append(user.toString()).append("\n");
        }
        return sb.toString().trim();
    }
}
