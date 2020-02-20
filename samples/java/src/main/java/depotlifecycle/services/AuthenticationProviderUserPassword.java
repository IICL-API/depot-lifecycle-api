package depotlifecycle.services;

import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UserDetails;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public class AuthenticationProviderUserPassword implements AuthenticationProvider {
    @Override
    public Publisher<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) {
        if (authenticationRequest.getIdentity().equals("fail")) {
            //if the user wants to test what a fail authentication responds
            return Flowable.just(new AuthenticationFailed());
        }

        //assume identity & secret are always correct for the purposes of this sample
        return Flowable.just(new UserDetails((String) authenticationRequest.getIdentity(), new ArrayList<>()));
    }
}
