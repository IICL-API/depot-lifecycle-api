package depotlifecycle;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.client.HttpClientConfiguration;
import io.micronaut.runtime.ApplicationConfiguration;
import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(DepotLifecycleConfiguration.PREFIX)
@Requires(property = DepotLifecycleConfiguration.PREFIX)
public class DepotLifecycleConfiguration extends HttpClientConfiguration {
    public static final String PREFIX = "depotlifecycle.client";

    private final DepotLifecycleConnectionPoolConfiguration connectionPoolConfiguration;
    private final DepotLifecycleHttp2Configuration http2Configuration;

    @Getter
    @Setter
    private String url;

    @Getter
    @Setter
    private String authorization;

    public DepotLifecycleConfiguration(ApplicationConfiguration applicationConfiguration, DepotLifecycleConnectionPoolConfiguration connectionPoolConfiguration, DepotLifecycleHttp2Configuration http2Configuration) {
        super(applicationConfiguration);
        this.connectionPoolConfiguration = connectionPoolConfiguration;
        this.http2Configuration = http2Configuration;
    }

    @Override
    public ConnectionPoolConfiguration getConnectionPoolConfiguration() {
        return connectionPoolConfiguration;
    }

    @Override
    public Http2ClientConfiguration getHttp2Configuration() {
        return http2Configuration;
    }

    @ConfigurationProperties(PREFIX)
    public static class DepotLifecycleConnectionPoolConfiguration extends HttpClientConfiguration.ConnectionPoolConfiguration {

    }

    @ConfigurationProperties(PREFIX)
    public static class DepotLifecycleHttp2Configuration extends HttpClientConfiguration.Http2ClientConfiguration {

    }
}
