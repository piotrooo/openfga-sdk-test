import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.openfga.OpenFGAContainer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SdkTest {
    @Test
    void sdkTestsRunner() {
        try (
                var network = Network.newNetwork();
                var openFGAContainer = new OpenFGAContainer("openfga/openfga:latest")
                        .withNetwork(network)
                        .withNetworkAliases("openfga")
        ) {
            openFGAContainer.start();

            List<String> dockerImageNames = List.of("openfga-java-sdk-app:latest", "openfga-js-sdk-app:latest");
            for (String dockerImageName : dockerImageNames) {
                try (var sdkAppContainer = new GenericContainer<>(dockerImageName)
                        .withExposedPorts(9000)
                        .waitingFor(Wait.forHttp("/health").forStatusCode(200))
                        .withNetwork(network)) {
                    sdkAppContainer.start();

                    String sdkAppBaseUrl = "http://%s:%d".formatted(sdkAppContainer.getHost(), sdkAppContainer.getMappedPort(9000));

                    String reportDirName = "karate-reports/" + dockerImageName.replace(":", "_");
                    Results results = Runner.path("classpath:features")
                            .systemProperty("sdkAppBaseUrl", sdkAppBaseUrl)
                            .reportDir(reportDirName)
                            .parallel(1);
                    assertThat(results.getFailCount()).as(results.getErrorMessages()).isZero();
                }
            }
        }
    }
}
