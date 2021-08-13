package ru.vershinin.config.sso;

import org.camunda.bpm.webapp.impl.security.auth.ContainerBasedAuthenticationFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.ForwardedHeaderFilter;

import javax.inject.Inject;
import java.util.Collections;

/**
 * Camunda Web application SSO configuration for usage with KeycloakIdentityProviderPlugin.
 */
@ConditionalOnMissingClass("org.springframework.test.context.junit.jupiter.SpringExtension")
@Configuration
@Order(SecurityProperties.BASIC_AUTH_ORDER - 10)
public class WebAppSecurityConfig extends WebSecurityConfigurerAdapter {

	@Inject
	private KeycloakLogoutHandler keycloakLogoutHandler;

	/**
	 * Указываем точки которые необходимо защитить
	 */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	
    	http
    	.csrf().ignoringAntMatchers("/api/**", "/engine-rest/**")//Позволяет указать, HttpServletRequest, что не следует использовать защиту CSRF
    	.and()
    	.requestMatchers().antMatchers("/**").and()//метод HttpSecurity , он не имеет ничего общего с authorizeRequests() .
				// http.antMatcher() говорит Spring настроить HttpSecurity только в том случае, если путь соответствует этому шаблону
        .authorizeRequests(
       		authorizeRequests ->
       		authorizeRequests
       		.antMatchers("/app/**", "/api/**", "/lib/**")//используется для применения авторизации к одному или нескольким путям, указанным в antMatchers()
       		.authenticated()//Указывает, что URL-адреса разрешены любым аутентифицированным пользователем.
       		.anyRequest()//условие для любого запроса
       		.permitAll()//позволит получить публичный доступ, то есть любой желающий может получить доступ к конечной точке
       		)
	    .oauth2Login()
	    .and()
	      .logout()
	      .logoutRequestMatcher(new AntPathRequestMatcher("/app/**/logout"))
	      .logoutSuccessHandler(keycloakLogoutHandler)
        ;
    }

	/**
	 * регистрирует бин и устанавливает параметры инициализации "authentication-provider"- указываем класс, где реализован KeycloakAuthenticationProvider
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
    public FilterRegistrationBean containerBasedAuthenticationFilter(){

        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new ContainerBasedAuthenticationFilter());
        filterRegistration.setInitParameters(Collections.singletonMap("authentication-provider", "ru.vershinin.config.sso.KeycloakAuthenticationProvider"));
        filterRegistration.setOrder(101); // убедитесь, что фильтр зарегистрирован после цепочки фильтров Spring Security
        filterRegistration.addUrlPatterns("/app/*");
        return filterRegistration;
    }
 
    // ForwardedHeaderFilter требуется для правильной сборки URL-адреса перенаправления для входа в систему OAUth2.
	// Без фильтра Spring генерирует URL-адрес HTTP, даже если доступ к маршруту контейнера осуществляется через HTTPS.
    @Bean
    public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        FilterRegistrationBean<ForwardedHeaderFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new ForwardedHeaderFilter());
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegistrationBean;
    }

	/**
	 * Этот слушатель предназначен для использования со сторонними сервлетами, например, JSF FacesServlet.
	 * В пределах собственной веб-поддержки Spring обработки DispatcherServlet вполне достаточно.
	 *
	 */
	@Bean
	@Order(0)
	public RequestContextListener requestContextListener() {
	    return new RequestContextListener();
	}
	
}