package sk.seges.acris.security.client.session;

import sk.seges.acris.security.shared.session.ClientSession;

import com.google.gwt.user.client.rpc.ServiceDefTarget;

public interface SessionServiceDefTarget extends ServiceDefTarget {

	void setSession(ClientSession clientSession);
}