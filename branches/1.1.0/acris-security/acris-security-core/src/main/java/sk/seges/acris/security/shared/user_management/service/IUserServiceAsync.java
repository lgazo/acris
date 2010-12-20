package sk.seges.acris.security.shared.user_management.service;

import sk.seges.acris.security.shared.exception.ServerException;
import sk.seges.acris.security.shared.session.ClientSession;
import sk.seges.acris.security.shared.user_management.domain.api.LoginToken;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous version of the user service used to basic authentication
 * functionality such as login and logout. Additionally service also provides
 * other functionality related to the users.
 * 
 * @author fat
 */
public interface IUserServiceAsync {

	/**
	 * Authenticate user represented with username and his credentials. Service
	 * method also set selected language to user preferences
	 * 
	 * @param username
	 *            unique username of the user
	 * @param password
	 *            plain text user password. In the future it should no be a
	 *            plain text
	 * @param language
	 *            user selected language represented as string (e.g. "sk",
	 *            "en_US")
	 * @param callback
	 *            Asynchronous GWT callback with client session filled with user
	 *            and sessionid as a result
	 */
	void login(LoginToken token, AsyncCallback<ClientSession> callback) throws ServerException;

	/**
	 * Logs out current logged user.
	 * 
	 * @param callback
	 *            Asynchronous GWT callback
	 */
	void logout(AsyncCallback<Void> callback) throws ServerException;
}