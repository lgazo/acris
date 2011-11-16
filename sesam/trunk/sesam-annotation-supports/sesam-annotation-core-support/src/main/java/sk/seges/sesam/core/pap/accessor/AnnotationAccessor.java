package sk.seges.sesam.core.pap.accessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor6;
import javax.lang.model.util.SimpleTypeVisitor6;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import sk.seges.sesam.core.pap.model.api.ClassSerializer;
import sk.seges.sesam.core.pap.model.mutable.utils.MutableProcessingEnvironment;
import sk.seges.sesam.core.pap.utils.MethodHelper;


public abstract class AnnotationAccessor {

	protected final MutableProcessingEnvironment processingEnv;
	
	public interface AnnotationFilter {
		boolean isAnnotationIgnored(AnnotationMirror annotation);
	}

	public AnnotationAccessor(MutableProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
	}
	
	public static class AnnotationTypeFilter implements AnnotationFilter {

		private final Class<?>[] types;
		private final boolean ignore;
		
		public AnnotationTypeFilter(boolean ignore, Class<?> ... types) {
			this.types = types;
			this.ignore = ignore;
		}
		
		@Override
		public boolean isAnnotationIgnored(AnnotationMirror annotation) {
			for (Class<?> type: types) {
				if (type.getName().equals(annotation.getAnnotationType().toString()) == ignore) {
					return true;
				}
			}
			return false;
		}
	}

	private static interface WrapsAnnotationMirror {
		AnnotationMirror getAnnotationMirror();
	}
	
	private static class ClassTypeVisitor extends SimpleTypeVisitor6<Class<?>, Void> {

		private final MutableProcessingEnvironment processingEnv;

		public ClassTypeVisitor(MutableProcessingEnvironment processingEnv) {
			this.processingEnv = processingEnv;
		}
		
		@Override
		public Class<?> visitArray(ArrayType t, Void p) {
			return visitDeclared((DeclaredType)t.getComponentType(), p);
		}
		
		@Override
		public Class<?> visitDeclared(DeclaredType t, Void p) {
			try {
				return Class.forName(processingEnv.getTypeUtils().toMutableType(t).toString(ClassSerializer.QUALIFIED, false));
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
	}
	
	private static class MirrorVisitor extends SimpleAnnotationValueVisitor6<Object, AnnotationValue> {
		
		private final MutableProcessingEnvironment processingEnv;
		private final TypeMirror type;
		
		public MirrorVisitor(MutableProcessingEnvironment processingEnv, TypeMirror type) {
			this.processingEnv = processingEnv;
			this.type = type;
		}

		@Override
		public Object visitEnumConstant(VariableElement c, AnnotationValue p) {
			for (Object o: p.accept(new ArrayValueVisitor(processingEnv), null).getEnumConstants()) {
				if (o.toString().equals(c.toString())) {
					return o;
				}
			}
			
			return null;
		}
		
		@Override
		public Object visitAnnotation(AnnotationMirror a, AnnotationValue p) {
			return enhance(a, p.accept(new ArrayValueVisitor(processingEnv), null), processingEnv);
		}
		
		@Override
		public Object visitArray(List<? extends AnnotationValue> vals, AnnotationValue p) {

			Object[] result = (Object[]) Array.newInstance(type.accept(new ClassTypeVisitor(processingEnv), null), vals.size());

			int i = 0;
			for (AnnotationValue val: vals) {
				result[i++] = val.accept(new MirrorVisitor(processingEnv, type), val);
			}

			return result;
		}
		
		@Override
		public Object visitBoolean(boolean b, AnnotationValue p) {
			return b;
		}
		
		@Override
		public Object visitByte(byte b, AnnotationValue p) {
			return b;
		}
		
		@Override
		public Object visitChar(char c, AnnotationValue p) {
			return c;
		}
		
		@Override
		public Object visitDouble(double d, AnnotationValue p) {
			return d;
		}
		
		@Override
		public Object visitFloat(float f, AnnotationValue p) {
			return f;
		}
		
		@Override
		public Object visitInt(int i, AnnotationValue p) {
			return i;
		}
		
		@Override
		public Object visitLong(long i, AnnotationValue p) {
			return i;
		}
		
		@Override
		public Object visitShort(short s, AnnotationValue p) {
			return s;
		}
		
		@Override
		public Object visitString(String s, AnnotationValue p) {
			return s;
		}
		
		@Override
		public Object visitType(TypeMirror t, AnnotationValue p) {
			try {
				return Class.forName(processingEnv.getTypeUtils().toMutableType(t).toString(ClassSerializer.QUALIFIED, false));
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
	};

	private static class ArrayValueVisitor extends SimpleAnnotationValueVisitor6<Class<?>, Void> {

		private final MutableProcessingEnvironment processingEnv;
		
		public ArrayValueVisitor(MutableProcessingEnvironment processingEnv) {
			this.processingEnv = processingEnv;
		}
		
		@Override
		public Class<?> visitAnnotation(AnnotationMirror a, Void p) {
			try {
				return Class.forName(processingEnv.getTypeUtils().toMutableType(a.getAnnotationType()).toString(ClassSerializer.QUALIFIED, false));
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
		
		@Override
		public Class<?> visitType(TypeMirror t, Void p) {
			return Class.class;
		}

		@Override
		public Class<?> visitBoolean(boolean b, Void p) {
			return Boolean.class;
		}
		
		@Override
		public Class<?> visitByte(byte b, Void p) {
			return Byte.class;
		}
		
		@Override
		public Class<?> visitChar(char c, Void p) {
			return Character.class;
		}
		
		@Override
		public Class<?> visitDouble(double d, Void p) {
			return Double.class;
		}
		
		@Override
		public Class<?> visitFloat(float f, Void p) {
			return Float.class;
		}
		
		@Override
		public Class<?> visitInt(int i, Void p) {
			return Integer.class;
		}
		
		@Override
		public Class<?> visitLong(long i, Void p) {
			return Long.class;
		}
		
		@Override
		public Class<?> visitShort(short s, Void p) {
			return Short.class;
		}
		
		@Override
		public Class<?> visitString(String s, Void p) {
			return String.class;
		}
		
		@Override
		public Class<?> visitEnumConstant(VariableElement c, Void p) {
			try {
				return Class.forName(processingEnv.getTypeUtils().toMutableType(c.asType()).toString(ClassSerializer.QUALIFIED, false));
			} catch (ClassNotFoundException e) {
				return null;
			}
		}			
	}

	private static class AnnotationMirrorProxy implements MethodInterceptor, WrapsAnnotationMirror {
				
		private final AnnotationMirror annotationMirror;
		private final MutableProcessingEnvironment processingEnv;
		
		public AnnotationMirrorProxy(AnnotationMirror annotationMirror, MutableProcessingEnvironment processingEnv) {
			this.annotationMirror = annotationMirror;
			this.processingEnv = processingEnv;
		}
		
		@Override
		public AnnotationMirror getAnnotationMirror() {
			return annotationMirror;
		}
		
		@Override
		public Object intercept(Object arg0, Method method, Object[] arg2, MethodProxy arg3) throws Throwable {
			if (method.getName().equals("toString")) {
				return annotationMirror.toString();
			}
			if (method.getName().equals("hashCode")) {
				return annotationMirror.hashCode();
			}
			if (method.getName().equals("getAnnotationMirror")) {
				return annotationMirror;
			}
			
			for (Entry<? extends ExecutableElement, ? extends AnnotationValue> annotationMethod: processingEnv.getElementUtils().getElementValuesWithDefaults(annotationMirror).entrySet()) {
				if (annotationMethod.getKey().getSimpleName().toString().equals(method.getName())) {
					AnnotationValue value = annotationMethod.getValue();
					Object result = value.accept(new MirrorVisitor(processingEnv, annotationMethod.getKey().getReturnType()), value);
					if (result == null) {
						return value;
					}
					
					return result;
				}
			}

			return null;
		}
	}
	
	public abstract boolean isValid();

	private static Object enhance(AnnotationMirror annotationMirror, Class<?> annotationClass, MutableProcessingEnvironment processingEnv) {
		List<Class<?>> interfaces = new ArrayList<Class<?>>();
		for (Class<?> interfaceClass: annotationClass.getInterfaces()) {
			interfaces.add(interfaceClass);
		}
		interfaces.add(WrapsAnnotationMirror.class);
		if (annotationClass.isInterface()) {
			interfaces.add(annotationClass);
		}
		return Enhancer.create(annotationClass, interfaces.toArray(new Class[] {}), new AnnotationMirrorProxy(annotationMirror, processingEnv));
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends Annotation> T getAnnotation(Element element, Class<T> annotationClass) {
		AnnotationMirror annotationMirror = getAnnotationMirror(element, new AnnotationTypeFilter(false, annotationClass));
		if (annotationMirror == null) {
			return null;
		}

		return (T)enhance(annotationMirror, annotationClass, processingEnv);
	}
	
	protected AnnotationMirror getAnnotationMirror(Element element, AnnotationFilter... annotationFilters) {

		for (AnnotationMirror annotationMirror: element.getAnnotationMirrors()) {
			for (AnnotationFilter annotationFilter: annotationFilters) {
				if (!annotationFilter.isAnnotationIgnored(annotationMirror)) {
					return annotationMirror;
				}
			}
		}

		return null;
	}

	protected <T> T toPojo(Annotation annotation, Class<T> clazz) {
		if (annotation instanceof WrapsAnnotationMirror) {
			return toPojo(((WrapsAnnotationMirror)annotation).getAnnotationMirror(), clazz);
		}
		
		throw new RuntimeException("Unsupported annotation: use " + AnnotationMirror.class.getCanonicalName() + " type of obtain annotation using getAnnotation method from " + 
				this.getClass().getCanonicalName() + "!");
	}
	
	protected <T> T toPojo(AnnotationMirror annotation, Class<T> clazz) {
		
		T newInstance = null;

		try {
			newInstance = clazz.newInstance();
		} catch (Exception e) {
			return null;
		}
		
		if (newInstance == null) {
			return null;
		}
		
		for (Entry<? extends ExecutableElement, ? extends AnnotationValue> annotationMethod: processingEnv.getElementUtils().getElementValuesWithDefaults(annotation).entrySet()) {
			AnnotationValue value = annotationMethod.getValue();
			Object result = value.accept(new MirrorVisitor(this.processingEnv, annotationMethod.getKey().getReturnType()), value);
			if (result == null) {
				result = value;
			}
			
			String methodName = MethodHelper.toMethod(MethodHelper.SETTER_PREFIX, annotationMethod.getKey().getSimpleName().toString());
			
			Method method = getMethodByName(clazz.getMethods(), methodName);
			if (method != null) {
				try {
					method.invoke(newInstance, result);
				} catch (Exception e) {
					return null;
				}
			}
		}
		
		return newInstance;
	}
	
	private Method getMethodByName(Method[] methods, String name) {
		for (Method method: methods) {
			if (method.getName().equals(name)) {
				return method;
			}
		}
		
		return null;
	}
}