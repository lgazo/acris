/**
 * 
 */
package sk.seges.corpis.server.domain.customer.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import sk.seges.corpis.server.domain.DBConstraints;
import sk.seges.corpis.server.domain.server.model.base.CompanyNameBase;
import sk.seges.corpis.shared.domain.validation.customer.CompanyCustomerFormCheck;

/**
 * @author ladislav.gazo
 */
@Embeddable
public class JpaCompanyName extends CompanyNameBase implements Serializable {
	private static final long serialVersionUID = 1517041015021653542L;
	
	@Column(length = DBConstraints.COMPANY_LENGTH)
	@NotNull(groups = CompanyCustomerFormCheck.class)
	@Size(min = 1, max = DBConstraints.COMPANY_LENGTH, groups = CompanyCustomerFormCheck.class)
	public String getCompanyName() {
		return super.getCompanyName();
	}

	@Column
	public String getDepartment() {
		return super.getDepartment();
	}

}