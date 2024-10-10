package depotlifecycle.view;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;

public class HtmlStatusException extends HttpStatusException {
    public HtmlStatusException(HttpStatus status, String message) {
        super(status, message);
    }

    public HtmlStatusException(HttpStatus status, Object body) {
        super(status, body);
    }
}
