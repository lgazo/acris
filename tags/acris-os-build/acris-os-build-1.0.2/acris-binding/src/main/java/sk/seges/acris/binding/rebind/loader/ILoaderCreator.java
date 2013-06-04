package sk.seges.acris.binding.rebind.loader;

import sk.seges.acris.binding.rebind.GeneratorException;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Creating loader for obtain data from server. User for XToMany binding, e.g.
 * filling the binding listbox or grid
 * 
 * @author fat
 * 
 */
public interface ILoaderCreator {

	/**
	 * Do all the generator work
	 * 
	 * @return name of the loader service
	 */
	String generateLoader(SourceWriter sourceWriter, JClassType classType,
			String propertyReference, String targetWidgetName, String beanProxyWrapper, String binding, JField field)
			throws GeneratorException;
	
	String[] getImports();
}