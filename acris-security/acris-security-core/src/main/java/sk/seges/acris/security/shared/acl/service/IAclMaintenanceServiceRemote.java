package sk.seges.acris.security.shared.acl.service;

import java.util.List;
import java.util.Map;

import sk.seges.acris.security.shared.user_management.domain.Permission;
import sk.seges.acris.security.shared.user_management.model.dto.GenericUserDTO;
import sk.seges.sesam.pap.service.annotation.RemoteServiceDefinition;

import com.google.gwt.user.client.rpc.RemoteService;

@RemoteServiceDefinition
public interface IAclMaintenanceServiceRemote extends RemoteService {
	
	void removeACLEntries(GenericUserDTO user, String[] securedClassNames);
	void removeACLEntries(List<Long> aclIds, String className, GenericUserDTO user);
	
	void resetACLEntries(String className, Long aclId, GenericUserDTO user, Permission[] authorities);
	void resetACLEntriesLoggedRole(String className, Long aclId, Permission[] authorities);
	void resetACLEntries(String className, GenericUserDTO user, Permission[] authorities, List<Long> aclIds);
	
	void setAclEntries(String className, Long aclId, GenericUserDTO user,Permission[] authorities);
	void setAclEntries(Map<String, List<Long>> acls, GenericUserDTO user,Permission[] authorities);
	
	boolean isVisibleFor(String sid, String className, Long aclId);
}
