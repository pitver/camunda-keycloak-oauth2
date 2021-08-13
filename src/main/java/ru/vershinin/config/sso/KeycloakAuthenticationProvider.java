package ru.vershinin.config.sso;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.rest.security.auth.AuthenticationResult;
import org.camunda.bpm.engine.rest.security.auth.impl.ContainerBasedAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * OAuth2 Authentication Provider for usage with Keycloak and KeycloakIdentityProviderPlugin. 
 */
public class KeycloakAuthenticationProvider extends ContainerBasedAuthenticationProvider {

    /**
     * Проверяет запрос на аутентификацию. Не может возвращать значение null, но всегда AuthenticationResult указывает, была ли проверка подлинности успешной,
     * и, если это правда, всегда предоставляет аутентифицированного пользователя.
     *
     * @param request - запрос на аутентификацию
     * @param engine - процесс,к которому обращается запрос. Может использоваться для аутентификации в службе идентификации движка.
     * @return - результат проверки
     */
    @Override
    public AuthenticationResult extractAuthenticatedUser(HttpServletRequest request, ProcessEngine engine) {

        // Извлечение атрибута имени пользователя токена OAuth2
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof OAuth2AuthenticationToken) || !(authentication.getPrincipal() instanceof OidcUser)) {
			return AuthenticationResult.unsuccessful();
		}
        String userId = ((OidcUser)authentication.getPrincipal()).getName();
        if (!StringUtils.hasLength(userId)) {
            return AuthenticationResult.unsuccessful();
        }

        // Authentication successful
        AuthenticationResult authenticationResult = new AuthenticationResult(userId, true);
        authenticationResult.setGroups(getUserGroups(userId, engine));

        return authenticationResult;
    }

    /**
     * Представляет группу, используемую в IdentityService.
     *
     * @param userId - Id пользователя
     * @param engine - процесс,к которому обращается запрос. Может использоваться для аутентификации в службе идентификации движка.
     * @return - возвращает группы с использованием плагина поставщика удостоверений Keycloak
     */
    private List<String> getUserGroups(String userId, ProcessEngine engine){
        List<String> groupIds = new ArrayList<>();
        engine.getIdentityService().createGroupQuery().groupMember(userId).list()
        	.forEach( g -> groupIds.add(g.getId()));
        return groupIds;
    }

}