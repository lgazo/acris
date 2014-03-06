package sk.seges.acris.security.server.spring.user_management.dao.user.api;

import sk.seges.acris.security.shared.user_management.domain.api.GroupAuthoritiesHolder;
import sk.seges.acris.security.shared.user_management.domain.api.UserPermission;
import sk.seges.corpis.server.domain.user.server.model.data.UserData;
import sk.seges.sesam.dao.ICrudDAO;
import sk.seges.sesam.dao.IEntityInstancer;

public interface IGenericUserDao<T extends UserData> extends ICrudDAO<T>, IEntityInstancer<T> {

	T findUser(String username, String webId);
	
	T findByUsername(String username);

	T persist(T user, GroupAuthoritiesHolder<? extends UserPermission> authoritiesHolder);

	T persist(T user, GroupAuthoritiesHolder<? extends UserPermission> authoritiesHolder, boolean addMode);

	T collectUserAuthorities(T user, GroupAuthoritiesHolder<? extends UserPermission> authoritiesHolder, boolean addMode);
}