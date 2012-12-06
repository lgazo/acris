package sk.seges.sesam.pap.converter.printer.converterprovider;

import sk.seges.sesam.core.pap.model.mutable.api.MutableTypeMirror;
import sk.seges.sesam.core.pap.model.mutable.api.MutableTypeVariable;
import sk.seges.sesam.core.pap.writer.FormattedPrintWriter;
import sk.seges.sesam.pap.converter.printer.AbstractObjectConverterProviderPrinter;
import sk.seges.sesam.pap.converter.printer.model.ConverterProviderPrinterContext;
import sk.seges.sesam.pap.model.model.ConverterTypeElement;
import sk.seges.sesam.pap.model.model.TransferObjectProcessingEnvironment;
import sk.seges.sesam.pap.model.model.api.domain.DomainDeclaredType;
import sk.seges.sesam.pap.model.model.api.domain.DomainType;
import sk.seges.sesam.pap.model.printer.converter.ConverterProviderPrinter;
import sk.seges.sesam.pap.model.resolver.ConverterConstructorParametersResolverProvider;
import sk.seges.sesam.shared.model.converter.ConvertedInstanceCache;

public abstract class AbstractDomainMethodConverterProviderPrinter extends AbstractObjectConverterProviderPrinter {

	protected AbstractDomainMethodConverterProviderPrinter(ConverterConstructorParametersResolverProvider parametersResolverProvider, 
			TransferObjectProcessingEnvironment processingEnv, FormattedPrintWriter pw, ConverterProviderPrinter converterProviderPrinter) {
		super(processingEnv, pw, converterProviderPrinter, parametersResolverProvider);
	}

	protected static final String DOMAIN_CLASS_PARAMETER_NAME = "domainClass";

	@Override
	public void print(ConverterProviderPrinterContext context) {

		if (!context.getDomain().getKind().isDeclared() || context.getConverterType() == null) {
			return;
		}
		
		if (!types.contains(context.getRawDomain().getCanonicalName())) {

			if (types.size() == 0) {
				String cacheParameterName = getConstructorParameterName(processingEnv.getTypeUtils().toMutableType(ConvertedInstanceCache.class));
				
				pw.println("public <" + ConverterTypeElement.DTO_TYPE_ARGUMENT_PREFIX + ", " + ConverterTypeElement.DOMAIN_TYPE_ARGUMENT_PREFIX +
						"> ", getTypedDtoConverter(), " getConverterForDomain(", Class.class.getSimpleName(), "<" + ConverterTypeElement.DOMAIN_TYPE_ARGUMENT_PREFIX + "> ",
						DOMAIN_CLASS_PARAMETER_NAME + ", ", ConvertedInstanceCache.class, " " + cacheParameterName + ") {");
				pw.println();
				pw.println("if (" + DOMAIN_CLASS_PARAMETER_NAME + " == null) {");
				pw.println("return null;");
				pw.println("}");
				pw.println();
			}
			
			types.add(context.getRawDomain().getCanonicalName());
			
			pw.println("if (", context.getRawDomain().clone().setTypeVariables(new MutableTypeVariable[] {}), ".class.isAssignableFrom(" + DOMAIN_CLASS_PARAMETER_NAME + ")) {");

			printResulConverter(context);
			
			pw.println(";");
			pw.println("}");
			pw.println();
		}
		
		printTypeVariables(context);
	}

	protected abstract void printResulConverter(ConverterProviderPrinterContext context);
	
	@Override
	protected void printType(MutableTypeMirror type, ConverterProviderPrinterContext context) {
		
		DomainType domainType = processingEnv.getTransferObjectUtils().getDomainType(type);
//		if (domainType.getKind().isDeclared() && domainType.getConverter() != null) {
			context = new ConverterProviderPrinterContext((DomainDeclaredType)domainType);
			print(context);
//		}
	}
}