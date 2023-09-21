package codesquard.app.api.chat;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.api.chat.response.ChatRoomCreateResponse;
import codesquard.app.api.errors.errorcode.ItemErrorCode;
import codesquard.app.api.errors.errorcode.MemberErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.domain.chat.ChatRoom;
import codesquard.app.domain.chat.ChatRoomRepository;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.member.MemberRepository;
import codesquard.app.domain.oauth.support.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatRoomService {

	private final ChatRoomRepository chatRoomRepository;
	private final MemberRepository memberRepository;
	private final ItemRepository itemRepository;

	@Transactional
	public ChatRoomCreateResponse createChatRoom(Long itemId, Principal sender) {
		log.info("채팅방 생성 서비스 요청 : itemId={}, sender={}", itemId, sender);

		Item item = findItemBy(itemId);
		Member senderMember = findMemberBy(sender.getMemberId());

		ChatRoom chatRoom = new ChatRoom(LocalDateTime.now(), senderMember, item);

		ChatRoom saveChatRoom = chatRoomRepository.save(chatRoom);
		log.debug("채팅방 저장 결과 : chatRoom={}", saveChatRoom);

		return ChatRoomCreateResponse.from(saveChatRoom);
	}

	private Item findItemBy(Long itemId) {
		return itemRepository.findById(itemId)
			.orElseThrow(() -> new RestApiException(ItemErrorCode.ITEM_NOT_FOUND));
	}

	private Member findMemberBy(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER));
	}
}
