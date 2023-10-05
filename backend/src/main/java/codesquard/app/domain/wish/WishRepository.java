package codesquard.app.domain.wish;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {

	default BooleanExpression equalMemberId(Long memberId) {
		if (memberId == null) {
			return null;
		}
		return QWish.wish.member.id.eq(memberId);
	}

	void deleteByItemId(Long itemId);

	boolean existsByMemberIdAndItemId(Long memberId, Long itemId);

	List<Wish> findAllByMemberId(Long memberId);
}
