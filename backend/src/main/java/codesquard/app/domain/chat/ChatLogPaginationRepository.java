package codesquard.app.domain.chat;

import static codesquard.app.domain.chat.QChatLog.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ChatLogPaginationRepository {

	private final JPAQueryFactory queryFactory;

	public List<ChatLog> searchBy(BooleanBuilder whereBuilder) {
		return queryFactory.selectFrom(chatLog)
			.where(whereBuilder)
			.orderBy(chatLog.id.asc(), chatLog.createdAt.asc())
			.fetch();
	}

	public BooleanExpression greaterThanChatLogId(Long chatLogId) {
		if (chatLogId == null) {
			return null;
		}
		return chatLog.id.gt(chatLogId);
	}

	public BooleanExpression equalChatRoomId(Long chatRoomId) {
		if (chatRoomId == null) {
			return null;
		}
		return chatLog.chatRoom.id.eq(chatRoomId);
	}
}
