package sk.seges.sesam.pap.service.model;

import java.util.ArrayList;
import java.util.List;

import sk.seges.sesam.core.pap.model.mutable.api.MutableDeclaredType;
import sk.seges.sesam.core.pap.model.mutable.delegate.DelegateMutableDeclaredType;
import sk.seges.sesam.core.pap.model.mutable.utils.MutableProcessingEnvironment;

public class ServiceConverterTypeElement extends DelegateMutableDeclaredType {

	public static final String SERVICE_CONVERTER_SUFFIX = "Converter";

	private final ServiceTypeElement serviceTypeElement;
	
	ServiceConverterTypeElement(ServiceTypeElement serviceTypeElement, MutableProcessingEnvironment processingEnv) {
		this.serviceTypeElement = serviceTypeElement;

		List<LocalServiceTypeElement> localServiceInterfaces = serviceTypeElement.getLocalServiceInterfaces();

		if (localServiceInterfaces != null && localServiceInterfaces.size() > 0) {
			List<MutableDeclaredType> interfaces = new ArrayList<MutableDeclaredType>();

			for (LocalServiceTypeElement localInterface : localServiceInterfaces) {
				interfaces.add(localInterface.getRemoteServiceInterface());
			}

			setInterfaces(interfaces);
		}
	}
	
	public ServiceTypeElement getServiceTypeElement() {
		return serviceTypeElement;
	}
	
	@Override
	protected MutableDeclaredType getDelegate() {
		return serviceTypeElement.clone().addClassSufix(SERVICE_CONVERTER_SUFFIX);
	}
}