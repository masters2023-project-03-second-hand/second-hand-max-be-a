package codesquard.app.domain.chat;

import java.util.ArrayList;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.category.CategoryFixedFactory;
import codesquard.app.api.item.ItemFixedFactory;
import codesquard.app.api.oauth.OauthFixedFactory;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;

class ChatRoomTest extends IntegrationTestSupport {

	@DisplayName("채팅방에 회원을 설정한다")
	@Test
	public void setMember() {
		// given
		Member member = OauthFixedFactory.createFixedMemberWithMemberTown();
		ChatRoom chatRoom = ChatRoomFixedFactory.createFixedChatRoom();
		// when
		chatRoom.setMember(member);
		// then
		Member saveMember = memberRepository.save(member);
		ChatRoom saveChatRoom = chatRoomRepository.save(chatRoom);

		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(saveChatRoom.getMember()).isEqualTo(saveMember);
			softAssertions.assertThat(saveMember.getChatRooms()).contains(saveChatRoom).hasSize(1);
			softAssertions.assertAll();
		});
	}

	@Transactional
	@DisplayName("채팅방에 상품을 설정한다")
	@Test
	public void setItem() {
		// given
		Member member = OauthFixedFactory.createFixedMemberWithMemberTown();
		ChatRoom chatRoom = ChatRoomFixedFactory.createFixedChatRoom();
		chatRoom.setMember(member);
		Category category = CategoryFixedFactory.createdFixedCategory();
		Item item = ItemFixedFactory.createFixedItem(member, category, new ArrayList<>(), new ArrayList<>(), 0L);
		// when
		chatRoom.setItem(item);
		// then
		categoryRepository.save(category);
		memberRepository.save(member);
		Item saveItem = itemRepository.save(item);
		ChatRoom saveChatRoom = chatRoomRepository.save(chatRoom);

		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(saveChatRoom.getItem()).isEqualTo(saveItem);
			softAssertions.assertThat(saveItem.getChatRooms()).contains(saveChatRoom).hasSize(1);
			softAssertions.assertAll();
		});
	}

	@DisplayName("채팅방에 채팅을 추가한다")
	@Test
	public void addChatLog() {
		// given
		Member member = OauthFixedFactory.createFixedMemberWithMemberTown();
		ChatRoom chatRoom = ChatRoomFixedFactory.createFixedChatRoom();
		chatRoom.setMember(member);
		Category category = CategoryFixedFactory.createdFixedCategory();
		Item item = ItemFixedFactory.createFixedItem(member, category, new ArrayList<>(), new ArrayList<>(), 0L);
		chatRoom.setItem(item);
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

