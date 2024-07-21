package responses;

public record RegisterResponse(
        String username,
        int authToken
) {
}
