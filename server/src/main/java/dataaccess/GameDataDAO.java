package dataaccess;

public interface GameDataDAO {
    public class MemoryGameDAO implements GameDataDAO {}
    public class SQLGameDAO implements GameDataDAO {}
}
