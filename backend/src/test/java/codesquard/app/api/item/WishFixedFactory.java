package codesquard.app.api.item;

import java.time.LocalDateTime;

import codesquard.app.domain.member.Member;
import codesquard.app.domain.wish.Wish;

public class WishFixedFactory {
	public static Wish createWish(Member member) {
		Wish interest = Wish.create(LocalDateTime.now());
		interest.setMember(member);
		return interest;
	}
}
