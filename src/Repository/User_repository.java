package Repository;

import Model.User;

import java.util.*;

public class User_repository {
    private Map<UUID, User> users;
//
    public boolean existByLogin(String suggestedLogin) {
        for (User u : users.values()) {
            if (u.getLogin().equals(suggestedLogin))
                return true;
        }
        return false;
    }

    public boolean existByPhone(String suggestedPhone) {
        for (User u : users.values()) {
            if (u.getPhoneNumber().equals(suggestedPhone))
                return true;
        }
        return false;
    }
}
