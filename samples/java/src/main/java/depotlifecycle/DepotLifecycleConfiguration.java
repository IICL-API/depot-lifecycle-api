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

    @Getter
    @Setter
    private String url;

    @Getter
    @Setter
    private String authorization;

    public DepotLifecycleConfiguration(ApplicationConfiguration applicationConfiguration, DepotLifecycleConnectionPoolConfiguration connectionPoolConfiguration) {
        super(applicationConfiguration);
        this.connectionPoolConfiguration = connectionPoolConfiguration;
    }

    @Override
    public ConnectionPoolConfiguration getConnectionPoolConfiguration() {
        return connectionPoolConfiguration;
    }

    @ConfigurationProperties(PREFIX)
    public static class DepotLifecycleConnectionPoolConfiguration extends HttpClientConfiguration.ConnectionPoolConfiguration {

    }
}
