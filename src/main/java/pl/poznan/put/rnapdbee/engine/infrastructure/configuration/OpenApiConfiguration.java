package pl.poznan.put.rnapdbee.engine.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI RnapdbeeApi(@Value("${application-version}") String appVersion) {
        Info rnapdbeeEngineApiInfo = new Info()
                .title("Rnapdbee Engine API")
                .version(appVersion);

        return new OpenAPI().info(rnapdbeeEngineApiInfo);
    }

    @Bean
    public GroupedOpenApi calculationControllerApi() {
        return GroupedOpenApi.builder()
                .group("Calculation controller")
                .pathsToMatch("/**")
                .packagesToScan("pl.poznan.put.rnapdbee.engine.calculation.control")
                .build();
    }
}
