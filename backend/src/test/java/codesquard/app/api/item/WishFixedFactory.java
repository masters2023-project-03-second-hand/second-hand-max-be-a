package codesquard.app.api.item;

import java.time.LocalDateTime;

import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.wish.Wish;

public class WishFixedFactory {
	public static Wish createWish(Member member, Item item) {
		return Wish.create(member, item, LocalDateTime.now());
	}
}
