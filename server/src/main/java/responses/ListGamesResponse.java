package responses;
import model.GameData;
import java.util.Collection;
import java.util.Objects;

public record ListGamesResponse(
        Collection<GameData> games
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListGamesResponse that = (ListGamesResponse) o;
        return Objects.equals(games, that.games);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(games);
    }
}
