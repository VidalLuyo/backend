package pe.edu.vallegrande.vg_ms_assistance.application.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(basePackages = "pe.edu.vallegrande.vg_ms_assistance.infrastructure.repository")
public class R2dbcConfig {
}
