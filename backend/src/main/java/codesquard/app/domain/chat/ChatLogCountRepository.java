package codesquard.app.domain.chat;

import static codesquard.app.domain.chat.QChatLog.*;
import static codesquard.app.domain.chat.QChatRoom.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
	 * 각각의 채팅방별 읽지 않은 메시지 개수를 가져옵니다.
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

		int chatRoomIdIdx = 0;
		int unreadMessageCountIdx = 1;
		return results.stream()
			.collect(Collectors.toMap(
				tuple -> tuple.get(chatRoomIdIdx, Long.class),
				tuple -> Optional.ofNullable(tuple.get(unreadMessageCountIdx, Long.class)).orElse(0L)
			));
	}

	private BooleanExpression isUnread() {
		return chatLog.readCount.goe(1);
	}

	private BooleanExpression notEqualsLoginId(String loginId) {
		return chatLog.sender.ne(loginId);
	}
}
