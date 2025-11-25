package pe.edu.vallegrande.vg_ms_assistance.application.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class OpenApiConfig implements WebFluxConfigurer {

    @Value("${server.port:9087}")
    private String serverPort;

    @Bean
    public OpenAPI apiInfo() {
        String serverUrl = detectEnvironmentUrl();
        String environmentName = detectEnvironmentName();

        return new OpenAPI()
                .addServersItem(new Server()
                        .url(serverUrl)
                        .description("Servidor de " + environmentName))
                .info(new Info()
                        .title("API REST DE NPH - Psychology & Welfare")
                        .description("Especificación de REST API services para el módulo de Psicología y Bienestar")
                        .license(new License()
                                .name("Valle Grande")
                                .url("https://vallegrande.edu.pe"))
                        .version("1.0.0"));
    }

    /**
     * Detecta el entorno de ejecución y retorna la URL apropiada
     */
    private String detectEnvironmentUrl() {
        // Detectar Gitpod
        String gitpodWorkspaceUrl = System.getenv("GITPOD_WORKSPACE_URL");
        if (gitpodWorkspaceUrl != null && !gitpodWorkspaceUrl.isEmpty()) {
            // Convertir https://workspace-url a https://port-workspace-url
            String workspaceId = gitpodWorkspaceUrl.replace("https://", "");
            return "https://" + serverPort + "-" + workspaceId + "/";
        }

        // Detectar GitHub Codespaces
        String codespaceName = System.getenv("CODESPACE_NAME");
        if (codespaceName != null && !codespaceName.isEmpty()) {
            // URL típica de Codespaces: https://codespace-name-port.app.github.dev
            return "https://" + codespaceName + "-" + serverPort + ".app.github.dev/";
        }

        // Detectar otros entornos cloud (Railway, Render, etc.)
        String railwayPublicDomain = System.getenv("RAILWAY_PUBLIC_DOMAIN");
        if (railwayPublicDomain != null && !railwayPublicDomain.isEmpty()) {
            return "https://" + railwayPublicDomain + "/";
        }

        String renderExternalUrl = System.getenv("RENDER_EXTERNAL_URL");
        if (renderExternalUrl != null && !renderExternalUrl.isEmpty()) {
            return renderExternalUrl + "/";
        }

        // Default: Localhost
        return "http://localhost:" + serverPort + "/";
    }

    /**
     * Detecta el nombre del entorno para la descripción
     */
    private String detectEnvironmentName() {
        if (System.getenv("GITPOD_WORKSPACE_URL") != null) {
            return "desarrollo (Gitpod)";
        }
        if (System.getenv("CODESPACE_NAME") != null) {
            return "desarrollo (GitHub Codespaces)";
        }
        if (System.getenv("RAILWAY_PUBLIC_DOMAIN") != null) {
            return "producción (Railway)";
        }
        if (System.getenv("RENDER_EXTERNAL_URL") != null) {
            return "producción (Render)";
        }
        return "desarrollo local";
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}