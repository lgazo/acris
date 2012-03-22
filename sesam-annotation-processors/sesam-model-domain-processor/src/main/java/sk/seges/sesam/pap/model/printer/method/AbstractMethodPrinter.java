package sk.seges.sesam.pap.model.printer.method;

import java.util.Iterator;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

import sk.seges.sesam.core.pap.model.mutable.api.MutableDeclaredType;
import sk.seges.sesam.core.pap.model.mutable.api.MutableTypeMirror;
import sk.seges.sesam.core.pap.model.mutable.api.MutableTypeVariable;
import sk.seges.sesam.core.pap.model.mutable.api.MutableWildcardType;
import sk.seges.sesam.core.pap.model.mutable.api.element.MutableExecutableElement;
import sk.seges.sesam.core.pap.utils.MethodHelper;
import sk.seges.sesam.core.pap.utils.TypeParametersSupport;
import sk.seges.sesam.core.pap.writer.FormattedPrintWriter;
import sk.seges.sesam.pap.model.context.api.TransferObjectContext;
import sk.seges.sesam.pap.model.model.ConfigurationTypeElement;
import sk.seges.sesam.pap.model.model.TransferObjectProcessingEnvironment;
import sk.seges.sesam.pap.model.model.api.domain.DomainDeclaredType;
import sk.seges.sesam.pap.model.model.api.domain.DomainType;
import sk.seges.sesam.pap.model.model.api.dto.DtoDeclaredType;
import sk.seges.sesam.pap.model.model.api.dto.DtoType;
import sk.seges.sesam.pap.model.printer.AbstractDtoPrinter;
import sk.seges.sesam.pap.model.printer.converter.ConverterProviderPrinter;
import sk.seges.sesam.pap.model.resolver.api.EntityResolver;
import sk.seges.sesam.pap.model.resolver.api.ConverterConstructorParametersResolver;
import sk.seges.sesam.pap.model.utils.TransferObjectHelper;

public abstract class AbstractMethodPrinter extends AbstractDtoPrinter {

	protected final RoundEnvironment roundEnv;
	protected final ConverterConstructorParametersResolver parametersResolver;
	protected final TypeParametersSupport typeParametersSupport;
	protected final TransferObjectHelper toHelper;
	
	protected ConverterProviderPrinter converterProviderPrinter;
	
	protected AbstractMethodPrinter(ConverterProviderPrinter converterProviderPrinter, ConverterConstructorParametersResolver parametersResolver, RoundEnvironment roundEnv, TransferObjectProcessingEnvironment processingEnv) {
		super(parametersResolver, processingEnv);
		this.roundEnv = roundEnv;
		this.converterProviderPrinter = converterProviderPrinter;
		this.parametersResolver = parametersResolver;
		this.toHelper = new TransferObjectHelper(processingEnv);
		this.typeParametersSupport = new TypeParametersSupport(processingEnv);
	}

	protected MutableTypeMirror castToDelegate(MutableTypeMirror domainNamedType) {
		TypeMirror domainType = processingEnv.getTypeUtils().fromMutableType(domainNamedType);
		
		if (domainType == null) {
			return domainNamedType;
		}
		
		DomainType domainTypeElement = processingEnv.getTransferObjectUtils().getDomainType(domainType);

		return castToDelegate(domainNamedType, domainTypeElement.getDomainDefinitionConfiguration() == null ? null : 
			domainTypeElement.getDomainDefinitionConfiguration().getDelegateConfigurationTypeElement());
		
	}
	
	public MutableTypeMirror castToDelegate(TypeMirror domainType) {
		DomainType domainTypeElement = processingEnv.getTransferObjectUtils().getDomainType(domainType);

		MutableTypeMirror domainNamedType = processingEnv.getTypeUtils().toMutableType(domainType);

		return castToDelegate(domainNamedType, domainTypeElement.getDomainDefinitionConfiguration() == null ? null : 
			domainTypeElement.getDomainDefinitionConfiguration().getDelegateConfigurationTypeElement());
	}
	
	protected MutableTypeMirror castToDelegate(MutableTypeMirror domainNamedType, ConfigurationTypeElement delegateConfigurationTypeElement) {

		if (delegateConfigurationTypeElement != null) {
			DomainDeclaredType replacementType = delegateConfigurationTypeElement.getDomain();
			
			if ((domainNamedType instanceof MutableDeclaredType) && ((MutableDeclaredType)domainNamedType).hasTypeParameters() && replacementType.hasTypeParameters()) {
				domainNamedType = replacementType.clone().setTypeVariables(((MutableDeclaredType)domainNamedType).getTypeVariables().toArray(new MutableTypeVariable[] {}));
			} else {
				domainNamedType = replacementType;
			}
		}
		
		if ((domainNamedType instanceof MutableDeclaredType) && ((MutableDeclaredType)domainNamedType).hasTypeParameters()) {

			MutableTypeVariable[] convertedParameters = new MutableTypeVariable[((MutableDeclaredType)domainNamedType).getTypeVariables().size()];
			
			int j = 0;
			for (MutableTypeVariable typeParameter: ((MutableDeclaredType)domainNamedType).getTypeVariables()) {
				
				if (typeParameter.getUpperBounds().size() > 0) {
					MutableTypeMirror[] convertedBounds = new MutableTypeMirror[typeParameter.getUpperBounds().size()];
					
					int i = 0;
					
					Iterator<? extends MutableTypeMirror> iterator = typeParameter.getUpperBounds().iterator();
					
					while (iterator.hasNext()) {
						convertedBounds[i++] = castToDelegate(iterator.next());
					}

					if (typeParameter.getVariable() != null && typeParameter.getVariable().equals(MutableWildcardType.WILDCARD_NAME)) {
						convertedParameters[j++] = processingEnv.getTypeUtils().getTypeVariable(null, convertedBounds);
					} else {
						convertedParameters[j++] = processingEnv.getTypeUtils().getTypeVariable(typeParameter.getVariable(), convertedBounds);
					}
				} else {
					convertedParameters[j++] = processingEnv.getTypeUtils().getTypeVariable(typeParameter.getVariable());
				}
			}
			
			domainNamedType = processingEnv.getTypeUtils().getDeclaredType((MutableDeclaredType)domainNamedType, convertedParameters);
		}
		
		return domainNamedType;
	}

	private static final String RESULT = "_result";
	
	protected void printDtoInstancer(FormattedPrintWriter pw, EntityResolver entityResolver, DtoType type) {
		pw.println(type," " + RESULT + " = new ", type, "();");
		if ((type instanceof DtoDeclaredType) && entityResolver.shouldHaveIdMethod((DomainDeclaredType) type.getDomain())) {
			MutableExecutableElement idMethod = ((DtoDeclaredType)type).getIdMethod(entityResolver);			
			pw.println(RESULT + "." + MethodHelper.toSetter(idMethod) + "((", idMethod.asType().getReturnType(), ")id);");
		}
		pw.println("return " + RESULT + ";");
	}

	protected boolean copy(TransferObjectContext context, FormattedPrintWriter pw, CopyMethodPrinter printer) {
		
		DomainType returnType = context.getDomainMethodReturnType();
		
		switch (returnType.getKind()) {
		case VOID:
			processingEnv.getMessager().printMessage(Kind.ERROR, "[ERROR] Unable to process " + context.getDtoFieldName() + ". Unsupported result type: " + 
					returnType.getKind());
			return false;
		case ARRAY:
			//TODO
			return false;
		}
		
		printer.printCopyMethod(context, pw);
		
		return true;
	}
}