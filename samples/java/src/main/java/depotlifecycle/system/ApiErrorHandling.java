package depotlifecycle.system;

import depotlifecycle.ErrorResponse;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpResponseFactory;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.security.authentication.AuthorizationException;

/**
 * Since this application is both a client & server, a global error handler cannot be defined.  Instead, define the error
 * handling on a per controller basis.
 */
public class ApiErrorHandling {
    public static HttpResponse<JsonError> notFound(HttpRequest request) {
        JsonError error = new JsonError("Not Found");

        return HttpResponse.<JsonError>notFound()
                .body(error);
    }

    public static HttpResponse<ErrorResponse> onSavedFailed(HttpRequest request, Throwable ex) {
        if(ex instanceof HttpStatusException statusException) {
            return HttpResponseFactory.INSTANCE.status(statusException.getStatus(), (ErrorResponse) statusException.getBody().orElse(null));
        }

        if(ex instanceof AuthorizationException) {
            return HttpResponse.unauthorized();
        }

        ErrorResponse error = new ErrorResponse();
        error.setCode("ERR000");
        error.setMessage(ex.getMessage());

        return HttpResponse.<ErrorResponse>badRequest().body(error);
    }
}
