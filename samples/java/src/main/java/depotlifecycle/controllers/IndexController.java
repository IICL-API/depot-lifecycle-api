package depotlifecycle.controllers;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;
import io.micronaut.views.ViewsRenderer;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/")
@RequiredArgsConstructor
@Hidden
public class IndexController {
    private final ViewsRenderer viewsRenderer;

    @View("index")
    @Get("/")
    HttpResponse<Map<String, Object>> index() {
        Map<String, Object> model = new HashMap<>();
        return HttpResponse.ok(model);
    }
}
