/**
 * 
 */
package sk.seges.corpis.server.domain.invoice.jpa;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

import sk.seges.corpis.server.domain.invoice.server.model.base.AccountableItemBase;
import sk.seges.corpis.server.domain.jpa.JpaPrice;

/**
 * @author eldzi
 */
@MappedSuperclass
public abstract class JpaAccountableItem extends AccountableItemBase {
	
	private static final long serialVersionUID = 366655167960921699L;

	private static final short DESCRIPTION_LENGTH = 250;
	
	public JpaAccountableItem() {
		setBasePrice(new JpaPrice());
	}

	@Embedded
	@Column(nullable = false)
	public JpaPrice getBasePrice() {
		return (JpaPrice)super.getBasePrice();
	}

	@Column(length = DESCRIPTION_LENGTH)
	public String getDescription() {
		return super.getDescription();
	}

	@Column(nullable = false)
	public Float getAmount() {
		return super.getAmount();
	}

	@Column
	public Float getWeight() {
		return super.getWeight();
	}

	@OneToOne
	public JpaUnit getUnit() {
		return (JpaUnit) super.getUnit();
	}

	@OneToOne
	public JpaVat getVat() {
		return (JpaVat) super.getVat();
	}

	@Column
	public String getExtId() {
		return super.getExtId();
	}
}