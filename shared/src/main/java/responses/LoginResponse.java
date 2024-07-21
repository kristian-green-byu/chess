package responses;

public record LoginResponse(
        String username,
        int authToken
) {
}
