package dataaccess;

public interface UserDataDAO {
    public class MemoryUserDAO implements UserDataDAO {}
    public class SQLiteUserDAO implements UserDataDAO {}
}
