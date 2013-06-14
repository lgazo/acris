package sk.seges.acris.security.server.spring.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.providers.ProviderManager;

import sk.seges.acris.security.server.spring.user_management.dao.user.api.IGenericUserDao;
import sk.seges.acris.security.server.spring.user_management.service.SpringUserService;
import sk.seges.acris.security.server.spring.user_management.service.provider.WebIdDaoAuthenticationProvider;
import sk.seges.corpis.server.domain.user.server.model.data.UserData;


public class AuthenticationManagerConfiguration {

	@Autowired
	private IGenericUserDao<UserData> genericUserDao;
	
	@Autowired
	private WebIdDaoAuthenticationProvider daoAuthenticationProvider;
	
	@Bean
	public SpringUserService userDetailsService() {
		return new SpringUserService(genericUserDao);
	}

	@Bean
	public ProviderManager authenticationManager() {
		ProviderManager providerManager = new ProviderManager();
		List<AuthenticationProvider> providers = new ArrayList<AuthenticationProvider>();
		providers.add(daoAuthenticationProvider);
		providerManager.setProviders(providers);
		return providerManager;
	}
}
