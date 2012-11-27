package sk.seges.acris.security.server.user_management.model.dto.configuration;

import sk.seges.acris.core.client.rpc.IDataTransferObject;
import sk.seges.acris.security.shared.core.user_management.domain.hibernate.HibernateSecurityRole;
import sk.seges.acris.security.user_management.server.model.data.RoleData;
import sk.seges.sesam.pap.model.annotation.GenerateEquals;
import sk.seges.sesam.pap.model.annotation.GenerateHashcode;
import sk.seges.sesam.pap.model.annotation.Mapping;
import sk.seges.sesam.pap.model.annotation.Mapping.MappingType;
import sk.seges.sesam.pap.model.annotation.TransferObjectMapping;

@TransferObjectMapping(domainClass = HibernateSecurityRole.class)
@Mapping(MappingType.AUTOMATIC)
@GenerateEquals(generate = false)
@GenerateHashcode(generate = false)
public interface SecurityRoleDTOConfiguration extends IDataTransferObject {

	static final String LOGGED_ROLE_NAME = "logged_user_role";

	static final String NONE = "none";
	static final String ALL_USERS = "*";
	static final String GRANT = "USER_ROLE";

	@TransferObjectMapping(domainClass = RoleData.class, configuration = SecurityRoleDTOConfiguration.class)
	public interface RoleDataConfiguration {}
	
}