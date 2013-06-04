/**
 * 
 */
package sk.seges.acris.binding.client.holder.validation;

import java.io.Serializable;
import java.util.Set;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.validation.client.InvalidConstraint;

/**
 * Interface determining whether there is possibility to validate the underlying
 * beans binding and work with it at graphical level. GUI widgets marked with it
 * would use e.g. different binding holder supporting validation. External
 * components would have access to highlighting - especially useful for
 * on-demand validation from form dialogs.
 * 
 * @author ladislav.gazo
 */
public interface ValidatableBeanBinding<T extends Serializable> {

	/**
	 * Highlights constraints that are invalid. If there are no invalid
	 * constraints all highlights will be cleared.
	 * 
	 * @param constraints
	 *            Invalid constraints for which property widgets should be
	 *            highlighted.
	 */
	void highlightConstraints(Set<InvalidConstraint<T>> constraints);

	/**
	 * Highlights or clears a constraint.
	 * 
	 * @param constraint
	 *            Invalid constraint for which the property widget should be
	 *            highlighted.
	 */
	void highlightConstraint(InvalidConstraint<T> constraint);

	/**
	 * Clears highlighted property widgets.
	 */
	void clearHighlight();

	/**
	 * Clears highlighted property widget.
	 */
	void clearHighlight(Widget widget);

	/**
	 * Returns widget tied to the property.
	 * 
	 * @param property
	 *            Property name
	 */
	Widget getPropertyWidget(String property);
}
