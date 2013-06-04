package sk.seges.acris.security.server.util;

import sk.seges.acris.security.user_management.server.model.base.RoleBase;

public class LoggedUserRole extends RoleBase {
	private static final long serialVersionUID = -8975142282892294530L;
	
	public LoggedUserRole() {
		setName(LoginConstants.LOGGED_ROLE_NAME);
	}
}
