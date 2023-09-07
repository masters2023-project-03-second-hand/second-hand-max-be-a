package codesquard.app.domain.chat;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import codesquard.app.IntegrationTestSupport;

class ChatLogTest extends IntegrationTestSupport {

	@DisplayName("채팅에 채팅방을 설정한다")
	@Test
	public void setChatRoom() {
		// given
		ChatRoom chatRoom = ChatRoomFixedFactory.createFixedChatRoom();
		ChatLog chatLog = ChatLogFixedFactory.createFixedChatLog(null);
		// when
		chatLog.setChatRoom(chatRoom);
		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(chatLog.getChatRoom()).isEqualTo(chatRoom);
			softAssertions.assertThat(chatRoom.getChatLogs()).contains(chatLog);
			softAssertions.assertAll();
		});
	}
}
