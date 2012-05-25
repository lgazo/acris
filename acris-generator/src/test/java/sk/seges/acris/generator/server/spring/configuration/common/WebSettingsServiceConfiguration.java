package sk.seges.acris.generator.server.spring.configuration.common;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import sk.seges.acris.generator.server.processor.ContentDataProvider;
import sk.seges.acris.generator.server.processor.factory.DefaultNodeParserFactory;
import sk.seges.acris.generator.server.processor.factory.HtmlProcessorFactory;
import sk.seges.acris.generator.server.processor.factory.PostProcessorActivatorFactory;
import sk.seges.acris.generator.server.processor.factory.api.NodeParserFactory;
import sk.seges.acris.generator.server.processor.post.AbstractElementPostProcessor;
import sk.seges.acris.site.server.service.IWebSettingsServiceLocal;
import sk.seges.acris.site.server.service.MockWebSettingsService;
import sk.seges.acris.site.server.service.builder.DefaultWebSettingsBuilder;
import sk.seges.acris.site.server.service.builder.IWebSettingsBuilder;


public class WebSettingsServiceConfiguration {

	private static final String MOCK_ANALYTICS_SCRIPT = "<script>test analytics script</script>";

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private PostProcessorActivatorFactory postProcessorActivatorFactory;

	@Autowired
	private ContentDataProvider contentMetaDataProvider;
	
	@Bean
	public IWebSettingsBuilder webSettingsBuilder() {
		return new DefaultWebSettingsBuilder();
	}
	
	@Bean
	public IWebSettingsServiceLocal webSettingsService() {
		return new MockWebSettingsService(webSettingsBuilder(), MOCK_ANALYTICS_SCRIPT, false);
	}
	
	@Bean
	public NodeParserFactory parserFactory() {
		return new DefaultNodeParserFactory();
	}
	
	@Bean
	public HtmlProcessorFactory htmlProcessorFactory() {
		Map<String, AbstractElementPostProcessor> abstractPostProcessors = this.applicationContext.getBeansOfType(AbstractElementPostProcessor.class);
		return new HtmlProcessorFactory(abstractPostProcessors.values(), postProcessorActivatorFactory, contentMetaDataProvider, parserFactory());
	}
}