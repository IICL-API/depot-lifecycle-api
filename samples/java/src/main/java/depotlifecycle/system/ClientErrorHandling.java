package depotlifecycle.system;

import depotlifecycle.view.HtmlStatusException;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.Set;

/**
 * Since this application is both a client & server, a global error handler cannot be defined.  Instead, define the error
 * handling on a per controller basis.
 */
public class ClientErrorHandling {
    public static <T> void validate(T cmd, Validator validator) {
        Set<ConstraintViolation<T>> violations = validator.validate(cmd);
        if (!violations.isEmpty()) {
            ConstraintViolation<T> violation = violations.iterator().next();
            String errorMessage = String.join(" ", violation.getPropertyPath().toString(), violation.getMessage());
            throw new HtmlStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }

    public static HtmlStatusException handleError(HttpClientResponseException e) {
        String responseBody = e.getResponse().getBody(String.class).orElse("No response body");
        String responseHeaders = e.getResponse().getHeaders().toString();
        int statusCode = e.getStatus().getCode();

        String errorMessage = String.format(
                "HttpClientResponseException: %s\nStatus Code: %d\nResponse Body: %s\nResponse Headers: %s",
                e.getMessage(), statusCode, responseBody, responseHeaders
        );

        String message = String.join(": ", "Unexpected Error Calling API", errorMessage);
        throw new HtmlStatusException(e.getStatus(), message);
    }
}
