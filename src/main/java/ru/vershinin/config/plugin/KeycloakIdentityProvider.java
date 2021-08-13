package ru.vershinin.config.plugin;

import org.camunda.bpm.extension.keycloak.plugin.KeycloakIdentityProviderPlugin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @ConfigurationProperties используется для привязки и проверки внешних свойств из файлов свойств, таких как .properties.file
 * prefix : префикс свойств, которые нужно привязать к этому объекту.
 */
@Component
@ConfigurationProperties(prefix="plugin.identity.keycloak")
public class KeycloakIdentityProvider extends KeycloakIdentityProviderPlugin {



}
