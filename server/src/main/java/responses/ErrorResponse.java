package responses;

import dataaccess.DataAccessException;

public record ErrorResponse(
        String message
) {
}
