package de.kreth.clubhelper.attendance.config;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@KeycloakConfiguration
public class UiSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    @Autowired
    private KeycloakClientRequestFactory factory;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
	KeycloakAuthenticationProvider keyCloakAuthProvider = keycloakAuthenticationProvider();
	keyCloakAuthProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
	auth.authenticationProvider(keyCloakAuthProvider);
    }

    @Bean
    public KeycloakConfigResolver keyCloakConfigResolver() {
	return new KeycloakSpringBootConfigResolver();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public KeycloakRestTemplate restTemplate() {
	return new KeycloakRestTemplate(factory);
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
	return new RegisterSessionAuthenticationStrategy(
		new SessionRegistryImpl());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
	super.configure(http);
	http.csrf().disable()
		.anonymous().disable()
		.authorizeRequests()
		.requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()
//		.and().authorizeRequests()
		.anyRequest().authenticated();

    }

}
