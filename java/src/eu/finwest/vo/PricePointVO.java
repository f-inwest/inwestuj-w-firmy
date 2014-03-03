package eu.finwest.vo;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class PricePointVO implements Serializable {
	private static final long serialVersionUID = 43247247287665L;
	
	@JsonProperty("value_displayed")	private String valueDisplayed;
	@JsonProperty("description") private String description;
	@JsonProperty("button_text") private String buttonText;
	@JsonProperty("action_url")	private String actionUrl;
	@JsonProperty("id")	private String sellerId;
	@JsonProperty("kwota")	private String amount;
	@JsonProperty("opis")	private String transactionDescClient;
	@JsonProperty("crc") private String crc;
	@JsonProperty("opis_sprzed") private String transactionDescSeller;
	@JsonProperty("pow_url") private String returnUrlSuccess;
	@JsonProperty("pow_url_blad") private String returnUrlFailure;
	@JsonProperty("email") private String userEmail;
	@JsonProperty("nazwisko") private String userName;
	@JsonProperty("telefon") private String userPhone;
	@JsonProperty("jezyk") private String paymentLanguage;
	@JsonProperty("md5sum") private String md5sum;
	
	public PricePointVO() {
	}

	public String getValueDisplayed() {
		return valueDisplayed;
	}

	public void setValueDisplayed(String valueDisplayed) {
		this.valueDisplayed = valueDisplayed;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getButtonText() {
		return buttonText;
	}

	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}

	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getTransactionDescClient() {
		return transactionDescClient;
	}

	public void setTransactionDescClient(String transactionDescClient) {
		this.transactionDescClient = transactionDescClient;
	}

	public String getCrc() {
		return crc;
	}

	public void setCrc(String crc) {
		this.crc = crc;
	}

	public String getTransactionDescSeller() {
		return transactionDescSeller;
	}

	public void setTransactionDescSeller(String transactionDescSeller) {
		this.transactionDescSeller = transactionDescSeller;
	}

	public String getReturnUrlSuccess() {
		return returnUrlSuccess;
	}

	public void setReturnUrlSuccess(String returnUrlSuccess) {
		this.returnUrlSuccess = returnUrlSuccess;
	}

	public String getReturnUrlFailure() {
		return returnUrlFailure;
	}

	public void setReturnUrlFailure(String returnUrlFailure) {
		this.returnUrlFailure = returnUrlFailure;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getPaymentLanguage() {
		return paymentLanguage;
	}

	public void setPaymentLanguage(String paymentLanguage) {
		this.paymentLanguage = paymentLanguage;
	}

	public String getMd5sum() {
		return md5sum;
	}

	public void setMd5sum(String md5sum) {
		this.md5sum = md5sum;
	}

	public String getActionUrl() {
		return actionUrl;
	}

	public void setActionUrl(String actionUrl) {
		this.actionUrl = actionUrl;
	}

}
