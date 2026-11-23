package interfaces;

import exception.CafeException;
import model.User;

public interface UserRepository {
    User login(String username, String password) throws CafeException;
}