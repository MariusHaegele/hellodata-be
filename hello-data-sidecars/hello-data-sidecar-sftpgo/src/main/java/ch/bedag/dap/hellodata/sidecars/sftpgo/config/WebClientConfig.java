package ch.bedag.dap.hellodata.sidecars.sftpgo.config;

import ch.bedag.dap.hellodata.sidecars.sftpgo.client.invoker.ApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Configuration
public class WebClientConfig {

    @Value("${hello-data.sftpgo.base-url}")
    private String baseUrl;

    @Bean
    public ApiClient apiClient(ObjectMapper objectMapper) {
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .filter(logRequest())
                .build();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        ApiClient apiClient = new ApiClient(webClient);
        apiClient.setBasePath(baseUrl + apiClient.getBasePath());
        return apiClient;
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            System.out.println("Request: " + clientRequest.method() + " " + clientRequest.url());
            clientRequest.headers()
                    .forEach((name, values) -> values.forEach(value -> System.out.println(name + ": " + value)));
            return Mono.just(clientRequest);
        });
    }
}
