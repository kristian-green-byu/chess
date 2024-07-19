package dataaccess;

public interface AuthDataDAO {
    public class MemoryAuthDAO implements AuthDataDAO {}
    public class SQLAuthDAO implements AuthDataDAO {}
}
