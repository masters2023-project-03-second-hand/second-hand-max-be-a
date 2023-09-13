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

class ChatRoomTest extends IntegrationTestSupport {

	@DisplayName("채팅방에 채팅을 추가한다")
	@Test
	public void addChatLog() {
		// given
		Member member = OauthFixedFactory.createFixedMember();
		Category category = CategoryFixedFactory.createdFixedCategory();
		Item item = ItemFixedFactory.createFixedItem(member, category, 0L);
		ChatRoom chatRoom = ChatRoomFixedFactory.createFixedChatRoom(member, item);
		ChatLog chatLog = ChatLogFixedFactory.createFixedChatLog(null);
		// when
		chatRoom.addChatLog(chatLog);
		// then
		categoryRepository.save(category);
		memberRepository.save(member);
		itemRepository.save(item);
		ChatRoom saveChatRoom = chatRoomRepository.save(chatRoom);

		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(saveChatRoom.getChatLogs()).contains(chatLog).hasSize(1);
			softAssertions.assertThat(chatLog.getChatRoom()).isEqualTo(chatRoom);
			softAssertions.assertAll();
		});
	}
}

