package depotlifecycle.security;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.ReactiveAuthenticationProvider;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.Optional;

@Singleton
public class AuthenticationProviderUserPassword<B, I, S> implements ReactiveAuthenticationProvider<HttpRequest<B>, I, S> {
    public static Optional<String> VALIDATE_USER_NAME = Optional.of("validate");
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationProviderUserPassword.class);

    @Override
    public Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<B> httpRequest, AuthenticationRequest<I, S> authenticationRequest) {
        LOG.info("Received Authentication Request");
        if (authenticationRequest.getIdentity().equals("fail")) {
            //if the user wants to test what a fail authentication responds
            return Flux.just(AuthenticationResponse.failure(AuthenticationFailureReason.USER_NOT_FOUND));
        }

        //assume identity & secret are always correct for the purposes of this sample
        return Flux.just(AuthenticationResponse.success((String) authenticationRequest.getIdentity()));
    }
}
