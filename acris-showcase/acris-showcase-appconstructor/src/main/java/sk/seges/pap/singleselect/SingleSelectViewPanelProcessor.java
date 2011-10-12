/**
 * 
 */
package sk.seges.pap.singleselect;

import java.lang.reflect.Constructor;
import java.util.List;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import sk.seges.acris.scaffold.model.domain.DomainModel;
import sk.seges.acris.scaffold.model.view.compose2.Singleselect;
import sk.seges.acris.scaffold.mvp.DefaultViewConfiguration;
import sk.seges.pap.ScaffoldConstant;
import sk.seges.pap.ScaffoldNameUtil;
import sk.seges.pap.printer.table.column.TableColumnPrinter;
import sk.seges.sesam.core.pap.FluentProcessor;
import sk.seges.sesam.core.pap.model.mutable.api.MutableDeclaredType;
import sk.seges.sesam.core.pap.writer.FormattedPrintWriter;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.view.client.ListDataProvider;

/**
 * @author ladislav.gazo
 */
public class SingleSelectViewPanelProcessor extends FluentProcessor {
	private final DefaultViewConfiguration defaultConfiguration = new DefaultViewConfiguration();

	public SingleSelectViewPanelProcessor() {
		reactsOn(Singleselect.class);
		setSuperClass(Composite.class);
		addImplementedInterface(new AlwaysRule() {
			@Override
			public List<MutableDeclaredType> getTypes(
					MutableDeclaredType typeElement) {
				ScaffoldNameUtil.prefixIfEnclosed(typeElement);
				typeElement.addPackageSufix("." + ScaffoldConstant.PRES_PKG);
				MutableDeclaredType displayInterface = typeElement.addClassSufix(Singleselect.class
								.getSimpleName() + ScaffoldConstant.DISPLAY_SUFFIX);
				return asList(displayInterface);
			}
		});
	}

	@Override
	protected MutableDeclaredType getResultType(MutableDeclaredType inputType) {
		ScaffoldNameUtil.prefixIfEnclosed(inputType);
		inputType.addPackageSufix("." + ScaffoldConstant.VIEW_PKG);
		return inputType.addClassSufix(Singleselect.class.getSimpleName()
				+ ScaffoldConstant.PANEL_SUFFIX);
	}

	@Override
	protected void processElement(ProcessorContext context) {
		FormattedPrintWriter pw = context.getPrintWriter();

		List<? extends TypeMirror> interfaces = context.getTypeElement()
				.getInterfaces();
		TypeMirror vtoMirror = findDomainModel(interfaces);
		if (vtoMirror == null) {
			throw new RuntimeException(
					"Yet not handled state where there is no background domain model. Maybe in future we can take methods from the interface directly");
		}

		MutableDeclaredType vto = (MutableDeclaredType) processingEnv
				.getTypeUtils().toMutableType(vtoMirror);
		int lastIndexOf = vto.getPackageName().lastIndexOf(".");
		vto.changePackage(vto.getPackageName().substring(0, lastIndexOf) + "."
				+ ScaffoldConstant.DTO_PKG);
		vto.replaceClassSuffix(ScaffoldConstant.MODEL_SUFFIX,
				ScaffoldConstant.DTO_SUFFIX);

		// fields
		pw.println("protected ", CellTable.class, "<", vto, "> table = new ",
				CellTable.class, "<", vto, ">();");

		pw.println("private ", ListDataProvider.class, "<", vto,
				"> provider = new ", ListDataProvider.class, "<", vto, ">();");

		// constructor
		pw.println("public ", context.getOutputType().getSimpleName(), "() {");
		pw.println("provider.addDataDisplay(table);");
		pw.println("initWidget(table);");

		// -- process properties
		final TableColumnElementAction action = new TableColumnElementAction(
				pw, vto);
		doForAllMembers(context.getTypeElement(), ElementKind.METHOD, action);

		pw.println("}");

		// methods
		pw.println("public void setValue(", List.class, "<", vto, "> values) {");
		pw.println("provider.setList(values);");
		pw.println("}");

		System.out.println("");
	}

	private TypeMirror findDomainModel(List<? extends TypeMirror> interfaces) {
		TypeMirror domaimModelMirror = processingEnv.getTypeUtils()
				.fromMutableType(
						processingEnv.getTypeUtils().toMutableType(
								DomainModel.class));

		for (TypeMirror ifaceMirror : interfaces) {
			if (processingEnv.getTypeUtils().isAssignable(ifaceMirror,
					domaimModelMirror)) {
				return ifaceMirror;
			}
		}

		return null;
	}

	public class TableColumnElementAction extends MethodAction {
		protected final FormattedPrintWriter pw;
		protected final MutableDeclaredType vto;

		public TableColumnElementAction(FormattedPrintWriter pw,
				MutableDeclaredType vto) {
			super();
			this.pw = pw;
			this.vto = vto;
		}

		@Override
		protected void doExecute(ExecutableElement element) {
			TypeMirror returnType = element.getReturnType();
			@SuppressWarnings("unchecked")
			Class<? extends TableColumnPrinter> renderComponent = (Class<? extends TableColumnPrinter>) defaultConfiguration
					.getRenderComponent(Singleselect.class,
							toElement(returnType).getQualifiedName().toString());
			if (renderComponent == null) {
				// TODO: handle the ultimate default
				return;
			}
			try {
				Constructor<? extends TableColumnPrinter> constructor = renderComponent
						.getConstructor(FormattedPrintWriter.class,
								MutableDeclaredType.class);
				TableColumnPrinter printer = constructor.newInstance(pw, vto);
				printer.print(element);
			} catch (Exception e) {
				throw new RuntimeException("Unable to construct printer for = "
						+ renderComponent, e);
			}
		}
	}
}
