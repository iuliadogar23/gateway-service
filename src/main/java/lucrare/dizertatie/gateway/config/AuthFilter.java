package lucrare.dizertatie.gateway.config;

import lucrare.dizertatie.gateway.entity.ContResponse;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final WebClient.Builder webClientBuilder;

    public AuthFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            try {
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new Exception("Missing authorization information");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

                String[] parts = authHeader.split(" ");

                if (parts.length != 2 || !"Bearer".equals(parts[0])) {
                    throw new Exception("Incorrect authorization structure");
                }

                return webClientBuilder.build()
                        .post()
                        .uri("http://authorization-service/cont/validateToken?token=" + parts[1])
                        .retrieve().bodyToMono(ContResponse.class)
                        .map(cont -> {
                            exchange.getRequest()
                                    .mutate()
                                    .header("X-auth-user-id", String.valueOf(cont.getId()));
                            return exchange;
                        }).flatMap(chain::filter);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        };
    }


    public static class Config {
        // empty class as I don't need any particular configuration
    }

}
