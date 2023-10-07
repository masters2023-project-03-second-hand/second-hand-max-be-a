package codesquard.app.domain.chat;

import static codesquard.app.domain.chat.QChatRoom.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ChatRoomPaginationRepository {

	private final JPAQueryFactory queryFactory;

	public Slice<ChatRoom> searchBySlice(BooleanBuilder whereBuilder, Pageable pageable) {
		List<ChatRoom> chatRooms = queryFactory.selectFrom(chatRoom)
			.where(whereBuilder)
			.orderBy(chatRoom.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();
		return checkLastPage(pageable, chatRooms);
	}

	private Slice<ChatRoom> checkLastPage(Pageable pageable, List<ChatRoom> chatRooms) {
		boolean hasNext = false;

		if (chatRooms.size() > pageable.getPageSize()) {
			hasNext = true;
			chatRooms.remove(pageable.getPageSize());
		}
		return new SliceImpl<>(chatRooms, pageable, hasNext);
	}

	public BooleanExpression equalItemId(Long itemId) {
		if (itemId == null) {
			return null;
		}
		return chatRoom.item.id.eq(itemId);
	}

	public BooleanExpression inItemIdsOfChatRoom(List<Long> itemIds) {
		if (itemIds == null) {
			return null;
		}
		return chatRoom.item.id.in(itemIds);
	}

	public BooleanExpression equalBuyerIdOfChatRoom(Long memberId) {
		if (memberId == null) {
			return null;
		}
		return chatRoom.buyer.id.eq(memberId);
	}
}
