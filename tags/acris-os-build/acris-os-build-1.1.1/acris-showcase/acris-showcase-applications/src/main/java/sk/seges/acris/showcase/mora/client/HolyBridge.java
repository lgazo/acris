package sk.seges.acris.showcase.mora.client;

import org.gwt.beansbinding.core.client.ext.BeanAdapterFactory;

import sk.seges.acris.binding.client.init.AdaptersRegistration;
import sk.seges.acris.binding.client.init.BeanWrapperIntrospector;
import sk.seges.acris.binding.client.init.BeansBindingInit;
import sk.seges.acris.binding.client.providers.wrapper.BeanWrapperAdapterProvider;
import sk.seges.acris.showcase.mora.client.gin.mock.MockGinjector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.gwtplatform.mvp.client.DelayedBindRegistry;

public class HolyBridge implements EntryPoint {

	public MockGinjector ginjector;

	@Override
	public void onModuleLoad() {

		ginjector = GWT.create(MockGinjector.class);

		BeanWrapperIntrospector.init();
		BeansBindingInit.init();

		BeanAdapterFactory.addProvider(new BeanWrapperAdapterProvider());

		AdaptersRegistration registration = GWT.create(AdaptersRegistration.class);
		registration.registerAllAdapters();

		DelayedBindRegistry.bind(ginjector);
		
		ginjector.getPlaceManager().revealCurrentPlace();
	}
}