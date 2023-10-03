package codesquard.app.domain.chat;

import static codesquard.app.domain.chat.QChatRoom.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
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
		log.info("채팅방 목록 조회 결과 : {}", chatRooms);
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

}
