package codesquard.app.api.item;

import java.time.LocalDateTime;

import codesquard.app.domain.interest.Interest;
import codesquard.app.domain.member.Member;

public class InterestFixedFactory {
	public static Interest createInterest(Member member) {
		Interest interest = Interest.create(LocalDateTime.now());
		member.addInterest(interest);
		return interest;
	}
}
