package codesquard.app.domain.chat;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.category.CategoryFixedFactory;
import codesquard.app.api.item.ItemFixedFactory;
import codesquard.app.api.oauth.OauthFixedFactory;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;

class ChatLogTest extends IntegrationTestSupport {

	@DisplayName("채팅에 채팅방을 설정한다")
	@Test
	public void setChatRoom() {
		// given
		Category category = CategoryFixedFactory.createdFixedCategory();
		Member member = OauthFixedFactory.createFixedMember();
		Item item = ItemFixedFactory.createFixedItem(member, category, 0L);
		ChatRoom chatRoom = ChatRoomFixedFactory.createFixedChatRoom(member, item);
		ChatLog chatLog = ChatLogFixedFactory.createFixedChatLog(null);
		// when
		chatLog.changeChatRoom(chatRoom);
		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(chatLog.getChatRoom()).isEqualTo(chatRoom);
			softAssertions.assertThat(chatRoom.getChatLogs()).contains(chatLog);
			softAssertions.assertAll();
		});
	}
}
