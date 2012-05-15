package sk.seges.corpis.server.domain.manufacture.jpa;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;

import sk.seges.corpis.server.domain.customer.jpa.JpaPersonName;
import sk.seges.corpis.server.domain.invoice.jpa.JpaAccountableItem;
import sk.seges.corpis.server.domain.invoice.server.model.data.AccountableItemData;
import sk.seges.corpis.server.domain.manufacture.server.model.base.ManufactureOrderBase;
import sk.seges.corpis.server.domain.manufacture.server.model.data.ManufactureItemData;
import sk.seges.corpis.server.domain.server.model.data.PersonNameData;

@SuppressWarnings("serial")
@Entity
@SequenceGenerator(name = JpaManufactureOrder.SEQ_MANUFACTURE_ORDER, sequenceName = "seq_manufacture_order", initialValue = 1)
@Table(name = "manufacture_order")
public class JpaManufactureOrder extends ManufactureOrderBase {

	protected static final String SEQ_MANUFACTURE_ORDER = "seqManufactureOrder";
	public static final String TABLE_PREFIX = "manufacture_";

	public JpaManufactureOrder() {
		setResponsiblePerson(new JpaPersonName());
	}
	
	@Override
	@ManyToOne(targetEntity = JpaManufactureItem.class)
	public ManufactureItemData getManufactureItem() {
		return super.getManufactureItem();
	}

	@Override
	@ManyToOne(targetEntity = JpaAccountableItem.class)
	public AccountableItemData getOrderBase() {
		return super.getOrderBase();
	}

	@Override
	@Column
	public Date getOrderDate() {
		return super.getOrderDate();
	}

	@Override
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = PersonNameData.FIRST_NAME, column = @Column(name = TABLE_PREFIX + PersonNameData.FIRST_NAME)),
			@AttributeOverride(name = PersonNameData.SURNAME, column = @Column(name = TABLE_PREFIX + PersonNameData.SURNAME)) })
	@Valid
	public PersonNameData getResponsiblePerson() {
		return super.getResponsiblePerson();
	}	
}