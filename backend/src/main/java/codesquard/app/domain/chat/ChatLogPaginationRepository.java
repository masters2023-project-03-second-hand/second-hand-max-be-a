package codesquard.app.domain.chat;

import static codesquard.app.domain.chat.QChatLog.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ChatLogPaginationRepository {

	private final JPAQueryFactory queryFactory;

	public Slice<ChatLog> searchBySlice(BooleanBuilder whereBuilder, Pageable pageable) {
		List<ChatLog> chatLogs = queryFactory.selectFrom(chatLog)
			.where(whereBuilder)
			.orderBy(chatLog.id.asc())
			.limit(pageable.getPageSize() + 1)
			.fetch();
		return checkLastPage(pageable, chatLogs);
	}

	private Slice<ChatLog> checkLastPage(Pageable pageable, List<ChatLog> chatLogs) {
		boolean hasNext = false;

		if (chatLogs.size() > pageable.getPageSize()) {
			hasNext = true;
			chatLogs.remove(pageable.getPageSize());
		}
		return new SliceImpl<>(chatLogs, pageable, hasNext);
	}

}
