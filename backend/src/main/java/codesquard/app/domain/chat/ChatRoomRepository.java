package codesquard.app.domain.chat;

import static codesquard.app.domain.chat.QChatRoom.*;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.querydsl.core.types.dsl.BooleanExpression;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

	default BooleanExpression equalItemId(Long itemId) {
		if (itemId == null) {
			return null;
		}

		return chatRoom.item.id.eq(itemId);
	}

	int deleteByItemId(Long itemId);

	@Query("select chatRoom.id from ChatRoom chatRoom where chatRoom.item.id = :itemId and chatRoom.buyer.id = :memberId")
	Optional<Long> findByItemIdAndMemberId(@Param("itemId") Long itemId, @Param("memberId") Long memberId);
}
