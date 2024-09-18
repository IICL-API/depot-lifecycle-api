package depotlifecycle.view;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import jakarta.inject.Singleton;

@Singleton
@Replaces(io.micronaut.http.server.exceptions.HttpStatusHandler.class)
public class HtmlStatusExceptionHandler implements ExceptionHandler<HtmlStatusException, HttpResponse<String>> {

    @Override
    public HttpResponse<String> handle(HttpRequest request, HtmlStatusException exception) {
        return HttpResponse.status(exception.getStatus())
                .contentType(MediaType.TEXT_PLAIN)
                .body(exception.getMessage());
    }
}