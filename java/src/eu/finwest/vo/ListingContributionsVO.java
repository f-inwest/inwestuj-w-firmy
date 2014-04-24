package eu.finwest.vo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class ListingContributionsVO extends BaseResultVO {
	@JsonProperty("listing") private ListingVO listing;
	@JsonProperty("submitted_contributions") private List<ContributionVO> submittedContributions;
	@JsonProperty("last_contributions") private List<ContributionVO> lastContributions;
	@JsonProperty("total_contributions") private List<UserContributionVO> totalContributions;

	public ListingVO getListing() {
		return listing;
	}
	public void setListing(ListingVO listing) {
		this.listing = listing;
	}
	public List<ContributionVO> getSubmittedContributions() {
		return submittedContributions;
	}
	public void setSubmittedContributions(List<ContributionVO> submittedContributions) {
		this.submittedContributions = submittedContributions;
	}
	public List<ContributionVO> getLastContributions() {
		return lastContributions;
	}
	public void setLastContributions(List<ContributionVO> lastContributions) {
		this.lastContributions = lastContributions;
	}
	public List<UserContributionVO> getTotalContributions() {
		return totalContributions;
	}
	public void setTotalContributions(List<UserContributionVO> totalContributions) {
		this.totalContributions = totalContributions;
	}
}
