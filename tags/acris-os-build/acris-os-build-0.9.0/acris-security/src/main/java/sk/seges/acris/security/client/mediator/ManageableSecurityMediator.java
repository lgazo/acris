package sk.seges.acris.security.client.mediator;

import sk.seges.acris.security.client.IManageableSecuredObject;

import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Widget;

public class ManageableSecurityMediator {
    
    public static void setVisible(Object manageableSecuredObject, Widget widget, boolean visible) {
        if(manageableSecuredObject instanceof IManageableSecuredObject) {
            ((IManageableSecuredObject) manageableSecuredObject).setVisible(widget, visible);
        }
    }
    
    public static void setEnabled(Object manageableSecuredObject, FocusWidget widget, boolean enabled) {
        if(manageableSecuredObject instanceof IManageableSecuredObject) {
            ((IManageableSecuredObject) manageableSecuredObject).setEnabled(widget, enabled);
        }
    }
}
