package codesquard.app.domain.chat;

import static codesquard.app.domain.chat.QChatLog.*;
import static codesquard.app.domain.chat.QChatRoom.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ChatLogCountRepository {

	private final JPAQueryFactory queryFactory;

	/**
	 *
	 * @return Map<Long, Long> : key=채팅방 등록번호, value=채팅방에 읽지 않은 메시지 개수
	 */
	public Map<Long, Long> countNewMessage(String loginId) {
		List<Tuple> results = queryFactory
			.select(chatLog.chatRoom.id, chatLog.chatRoom.id.count())
			.from(chatLog)
			.join(chatLog.chatRoom, chatRoom)
			.where(isUnread().and(notEqualsLoginId(loginId)))
			.groupBy(chatLog.chatRoom.id)
			.fetch();

		return results.stream()
			.collect(Collectors.toMap(
				tuple -> tuple.get(0, Long.class), // chatRoomId
				tuple -> tuple.get(1, Long.class) // newMessageCount
			));
	}

	private BooleanExpression isUnread() {
		return chatLog.isRead.eq(false);
	}

	private BooleanExpression notEqualsLoginId(String loginId) {
		return chatLog.sender.ne(loginId);
	}
}
