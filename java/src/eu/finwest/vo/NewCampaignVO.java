package eu.finwest.vo;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class NewCampaignVO extends BaseResultVO {
	@JsonProperty("new_campaign") private CampaignVO newCampaign;

	public CampaignVO getNewCampaign() {
		return newCampaign;
	}

	public void setNewCampaign(CampaignVO newCampaign) {
		this.newCampaign = newCampaign;
	}
}
