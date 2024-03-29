package eu.finwest.vo;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

import eu.finwest.web.UserMgmtFacade;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE,
		fieldVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class PrivateMessageUserListVO extends BaseResultVO implements UserDataUpdatableContainer {
	@JsonProperty("users") private List<PrivateMessageUserVO> messages;
	@JsonProperty("messages_props")	private ListPropertiesVO messagesProperties;
	
	public void updateUserData() {
		List<UserDataUpdatable> updatable = new ArrayList<UserDataUpdatable>();
		if (messages != null) updatable.addAll(messages);
		
		UserMgmtFacade.instance().updateUserData(updatable);
	}
	public List<PrivateMessageUserVO> getMessages() {
		return messages;
	}
	public void setMessages(List<PrivateMessageUserVO> messages) {
		this.messages = messages;
	}
	public ListPropertiesVO getMessagesProperties() {
		return messagesProperties;
	}
	public void setMessagesProperties(ListPropertiesVO messagesProperties) {
		this.messagesProperties = messagesProperties;
	}
}
