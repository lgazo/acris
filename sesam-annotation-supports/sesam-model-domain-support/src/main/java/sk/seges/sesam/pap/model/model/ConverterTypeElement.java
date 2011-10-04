package sk.seges.sesam.pap.model.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;

import sk.seges.sesam.core.pap.model.ParameterElement;
import sk.seges.sesam.core.pap.model.mutable.api.MutableDeclaredType;
import sk.seges.sesam.core.pap.model.mutable.api.MutableTypeVariable;
import sk.seges.sesam.core.pap.structure.DefaultPackageValidatorProvider;
import sk.seges.sesam.core.pap.structure.api.PackageValidatorProvider;
import sk.seges.sesam.core.pap.utils.ProcessorUtils;
import sk.seges.sesam.pap.model.context.api.TransferObjectContext;
import sk.seges.sesam.pap.model.model.api.GeneratedClass;
import sk.seges.sesam.pap.model.resolver.api.ParametersResolver;
import sk.seges.sesam.shared.model.converter.BasicCachedConverter;
import sk.seges.sesam.shared.model.converter.api.DtoConverter;

public class ConverterTypeElement extends TomBaseDeclaredType implements GeneratedClass {

	public static final String DEFAULT_SUFFIX = "Converter";	
	public static final String DEFAULT_CONFIGURATION_SUFFIX = "Configuration";
	public static final String DTO_TYPE_ARGUMENT_PREFIX = "DTO";
	public static final String DOMAIN_TYPE_ARGUMENT_PREFIX = "DOMAIN";

	private final TypeElement converterTypeElement;
	private final ConfigurationTypeElement configurationTypeElement;
	
	private final boolean generated;
	
	ConverterTypeElement(ConfigurationTypeElement configurationTypeElement, TypeElement converterTypeElement, TransferObjectProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
		super(processingEnv, roundEnv);
		this.converterTypeElement = converterTypeElement;
		this.configurationTypeElement = configurationTypeElement;
		this.generated = false;
		
		initialize();
	}
	
	public ConverterTypeElement(ConfigurationTypeElement configurationTypeElement, TransferObjectProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
		super(processingEnv, roundEnv);
		this.generated = true;
		this.converterTypeElement = null;
		this.configurationTypeElement = configurationTypeElement;

		setKind(MutableTypeKind.CLASS);
		setSuperClass(processingEnv.getTypeUtils().getDeclaredType(
				(MutableDeclaredType)processingEnv.getTypeUtils().toMutableType(BasicCachedConverter.class), configurationTypeElement.getDto(), configurationTypeElement.getDomain()));
	}

	protected MutableDeclaredType getDelegate() {
		if (converterTypeElement != null) {
			return (MutableDeclaredType) getMutableTypesUtils().toMutableType((DeclaredType)converterTypeElement.asType());
		}
		return getGeneratedConverterTypeFromConfiguration(configurationTypeElement);
	};

	private void initialize() {
		if (this.hasVariableParameterTypes() && configurationTypeElement.getDomain().hasTypeParameters()) {

			MutableTypeVariable[] converterParameters = new MutableTypeVariable[configurationTypeElement.getDomain().getTypeVariables().size() * 2];
			
			int i = 0;
			
			for (MutableTypeVariable typeVariable: configurationTypeElement.getDto().getTypeVariables()) {
				converterParameters[i] = typeVariable;
				i++;
			}

			for (MutableTypeVariable typeVariable: configurationTypeElement.getDomain().getTypeVariables()) {
				converterParameters[i] = typeVariable;
				i++;
			}
			
			setDelegate(this.clone().setTypeVariables(converterParameters));
		}
	}
	
	protected PackageValidatorProvider getPackageValidationProvider() {
		return new DefaultPackageValidatorProvider();
	}

	private List<MutableTypeVariable> copyTypeArguments(String prefix, DeclaredType declaredType, MutableDeclaredType referenceType) {
		
		List<MutableTypeVariable> result = new ArrayList<MutableTypeVariable>();
		
		Iterator<? extends MutableTypeVariable> iterator = referenceType.getTypeVariables().iterator();
		
		int i = 0;
		while (iterator.hasNext()) {
			if (i >= declaredType.getTypeArguments().size()) {
				break;
			}
			i++;
			MutableTypeVariable typeParameter = iterator.next();
			
			String name = typeParameter.getVariable();
			
			if (name != null && name.length() > 0) {
				result.add(typeParameter.clone().setVariable(prefix + "_" + name));
			}
		}
		
		return result;
	}

	private MutableDeclaredType getGeneratedConverterTypeFromConfiguration(ConfigurationTypeElement configurationTypeElement) {

		Element configurationElement = configurationTypeElement.asElement();
		
		if (!configurationElement.asType().getKind().equals(TypeKind.DECLARED)) {
			return null;
		}
		
		DeclaredType declaredType = (DeclaredType)configurationTypeElement.asElement().asType();
		
		TransferObjectConfiguration transferObjectConfiguration = new TransferObjectConfiguration((TypeElement)declaredType.asElement(), processingEnv);
		
		TypeElement converter = transferObjectConfiguration.getConverter();
		if (converter != null) {
			return null;
		}

		TypeElement domainType = transferObjectConfiguration.getDomain();
		
		MutableDeclaredType configurationNameType = getMutableTypesUtils().toMutableType((DeclaredType)configurationTypeElement.asElement().asType());
		
		//Remove configuration suffix if it is there - just to have better naming convention
		String simpleName = configurationNameType.getSimpleName();
		if (simpleName.endsWith( DEFAULT_CONFIGURATION_SUFFIX) && simpleName.length() > DEFAULT_CONFIGURATION_SUFFIX.length()) {
			simpleName = simpleName.substring(0, simpleName.lastIndexOf(DEFAULT_CONFIGURATION_SUFFIX));
			configurationNameType = configurationNameType.setSimpleName(simpleName);
		}
		
		MutableDeclaredType converterNameType = configurationNameType.addClassSufix(DEFAULT_SUFFIX);
		
		if (domainType.getTypeParameters().size() > 0) {
			
			MutableDeclaredType referenceType = getMutableTypesUtils().toMutableType((DeclaredType)domainType.asType());
			
			//there are type parameters, so they should be passed into the converter definition itself			
			List<MutableTypeVariable> typeParameters = copyTypeArguments(DTO_TYPE_ARGUMENT_PREFIX, (DeclaredType)domainType.asType(), referenceType);
			typeParameters.addAll(copyTypeArguments(DOMAIN_TYPE_ARGUMENT_PREFIX, (DeclaredType)domainType.asType(), referenceType));

			converterNameType.setTypeVariables(typeParameters.toArray(new MutableTypeVariable[] {}));
		}
		
		return converterNameType;
	}
	
	@Override
	public boolean isGenerated() {
		return generated;
	}
	
	public TypeElement asElement() {
		return converterTypeElement;
	}
	
	public ConfigurationTypeElement getConfiguration() {
		return configurationTypeElement;
	}

	private ConverterParameter toConverterParameter(ParameterElement parameter) {
		ConverterParameter converterParameter = new ConverterParameter();
		converterParameter.setType(parameter.getType());
		converterParameter.setName(parameter.getName());
		converterParameter.setConverter(this);
		converterParameter.setConverter(parameter.isConverter());
		return converterParameter;
	}
	
	private ConverterParameter toConverterParameter(VariableElement constructorParameter) {
		ConverterParameter converterParameter = new ConverterParameter();
		TypeElement dtoConverterTypeElement = processingEnv.getElementUtils().getTypeElement(DtoConverter.class.getCanonicalName());
		converterParameter.setConverter(ProcessorUtils.implementsType(constructorParameter.asType(), dtoConverterTypeElement.asType()));
		converterParameter.setType(getMutableTypesUtils().toMutableType(constructorParameter.asType()));
		converterParameter.setName(constructorParameter.getSimpleName().toString());
		converterParameter.setConverter(this);
		return converterParameter;
	}

	private List<ExecutableElement> getSortedConstructorMethods(TypeElement element) {
		List<ExecutableElement> constructors = ElementFilter.constructorsIn(element.getEnclosedElements());
		Collections.sort(constructors, new Comparator<ExecutableElement>() {

			@Override
			public int compare(ExecutableElement o1, ExecutableElement o2) {
				return o1.getParameters().size() - o2.getParameters().size();
			}
		});
		
		return constructors;
	}
	
	public List<ConverterParameter> getConverterParameters(ParametersResolver parametersResolver) {

		List<ConverterParameter> parameters = new LinkedList<ConverterParameter>();

		if (!isGenerated()) {
			TypeElement converterTypeElement = processingEnv.getElementUtils().getTypeElement(getCanonicalName());
			List<ExecutableElement> constructors = getSortedConstructorMethods(converterTypeElement);

			if (constructors != null && constructors.size() > 0) {
				List<? extends VariableElement> constructorParameters = constructors.get(0).getParameters();

				for (VariableElement constructorParameter : constructorParameters) {
					parameters.add(toConverterParameter(constructorParameter));
				}
			}
		} else {
			TypeElement cachedConverterType = processingEnv.getElementUtils().getTypeElement(BasicCachedConverter.class.getCanonicalName());
			List<ExecutableElement> constructors = getSortedConstructorMethods(cachedConverterType);

			if (constructors != null && constructors.size() > 0) {
				List<? extends VariableElement> constructorParameters = constructors.get(0).getParameters();

				for (VariableElement constructorParameter : constructorParameters) {
					parameters.add(toConverterParameter(constructorParameter));
				}
			}

			ParameterElement[] constructorAditionalParameters = parametersResolver.getConstructorAditionalParameters(getConfiguration().getDomain().asType());

			for (ParameterElement constructorAditionalParameter: constructorAditionalParameters) {
				parameters.add(toConverterParameter(constructorAditionalParameter));
			}
		}

		return parameters;
	}
	
	public DomainTypeElement getDomain() {
		if (getConfiguration() == null) {
			return null;
		}
		
		return getConfiguration().getDomain();
	}
	
	public List<String> getLocalConverters() {
		TypeElement domainElement = getDomain().asElement();

		List<String> result = new ArrayList<String>();

		if (domainElement == null) {
			return result;
		}
		
		for (int i = 0; i < domainElement.getTypeParameters().size(); i++) {
			result.add(TransferObjectContext.LOCAL_CONVERTER_NAME + i);
		}

		return result;
	}
}