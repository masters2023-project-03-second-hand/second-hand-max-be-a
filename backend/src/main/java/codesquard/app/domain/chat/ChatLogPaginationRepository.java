package codesquard.app.domain.chat;

import static codesquard.app.domain.chat.QChatLog.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
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
}
