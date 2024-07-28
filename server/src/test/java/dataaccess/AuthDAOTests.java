package dataaccess;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthDAOTests {
    @Test
    public void init() throws DataAccessException {
        AuthDAO authDAO = new SQLAuthDAO();
    }
}
