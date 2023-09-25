package codesquard.app.domain.chat;

import static codesquard.app.domain.chat.QChatRoom.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ChatRoomPaginationRepository {

	private final JPAQueryFactory queryFactory;

	public Slice<ChatRoom> searchBySlice(Long lastChatRoomId, Pageable pageable) {
		List<ChatRoom> chatRooms = queryFactory.selectFrom(chatRoom)
			.where(
				lessThanChatRoomId(lastChatRoomId)
			)
			.orderBy(chatRoom.id.desc())
			.limit(pageable.getPageSize() + 1)
			.fetch();
		return checkLastPage(pageable, chatRooms);
	}

	private BooleanExpression lessThanChatRoomId(Long chatRoomId) {
		if (chatRoomId == null) {
			return null;
		}
		return chatRoom.id.lt(chatRoomId);
	}

	private Slice<ChatRoom> checkLastPage(Pageable pageable, List<ChatRoom> chatRooms) {
		boolean hasNext = false;

		if (chatRooms.size() > pageable.getPageSize()) {
			hasNext = true;
			chatRooms.remove(pageable.getPageSize());
		}
		return new SliceImpl<>(chatRooms, pageable, hasNext);
	}

}
