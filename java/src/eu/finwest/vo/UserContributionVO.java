package eu.finwest.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Logger;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import eu.finwest.datamodel.Contribution;
import eu.finwest.datamodel.SBUser;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class UserContributionVO implements Serializable {
	private static final long serialVersionUID = 846104561484524L;
	
	private static final Logger log = Logger.getLogger(UserContributionVO.class.getName());
	
	@JsonProperty("contributor_id") private String contributor;
	@JsonProperty("contributor_avatar") private String contributorAvatar;
	@JsonProperty("contributor_username") private String contributorName;
	private double moneyValue;
	@JsonProperty("total_money") private String money;
	private int minutes;
	@JsonProperty("total_hours") private String hours;
	private double financialValueDouble;
	@JsonProperty("financial_value") private String financialValue;
	@JsonProperty("calculation_date") private Date date;
	private long daysSinceZero;
	
	public UserContributionVO() {
	}
	
	public UserContributionVO(SBUser user) {
		this.contributor = user.getWebKey();
		this.contributorAvatar = user.avatarUrl;
		this.contributorName = user.nickname;
		this.date = new Date();
		daysSinceZero = this.date.getTime() / (24 * 60 * 60 * 1000);
	}
	
	public void addContribution(Contribution c) {
		DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
		if (c.money > 0) {
			moneyValue += c.money;
			log.info("Contribution money: " + c.money + ", "
					+ dateFormatter.print(c.date.getTime()) + " " + dateFormatter.print(date.getTime())
					+ " (" + (this.daysSinceZero - c.daysSinceZero) + " days), value: "
					+ (c.money * (1.0 + c.interestPerDay * 0.0001 * (this.daysSinceZero - c.daysSinceZero))));
			financialValueDouble += c.money * (1.0 + c.interestPerDay * 0.0001 * (this.daysSinceZero - c.daysSinceZero));
		}
		if (c.minutes > 0) {
			minutes += c.minutes;
			log.info("Contribution time: " + c.minutes + "m, "
					+ dateFormatter.print(c.date.getTime()) + " " + dateFormatter.print(date.getTime())
					+ " (" + (this.daysSinceZero - c.daysSinceZero) + " days), value: "
					+ (c.minutes / 60.0 * c.perHour * (1.0 + c.interestPerDay * 0.0001 * (this.daysSinceZero - c.daysSinceZero))));
			financialValueDouble += c.minutes / 60.0 * c.perHour * (1.0 + c.interestPerDay * 0.0001 * (this.daysSinceZero - c.daysSinceZero));
		}
	}
	
	public void updateTextValues() {
		this.hours = String.format("%.1f", ((float)this.minutes) / 60.0);
		this.money = "" + moneyValue;
		this.financialValue = String.format("%.2f", this.financialValueDouble);
	}

	public String getContributor() {
		return contributor;
	}

	public void setContributor(String contributor) {
		this.contributor = contributor;
	}

	public String getContributorAvatar() {
		return contributorAvatar;
	}

	public void setContributorAvatar(String contributorAvatar) {
		this.contributorAvatar = contributorAvatar;
	}

	public String getContributorName() {
		return contributorName;
	}

	public void setContributorName(String contributorName) {
		this.contributorName = contributorName;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getFinancialValue() {
		return financialValue;
	}

	public void setFinancialValue(String financialValue) {
		this.financialValue = financialValue;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
