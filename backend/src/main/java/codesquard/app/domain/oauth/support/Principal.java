package codesquard.app.domain.oauth.support;

import java.util.Optional;

import codesquard.app.domain.chat.ChatRoom;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;
import io.jsonwebtoken.Claims;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Principal {

	private Long memberId;
	private String email;
	private String loginId;
	private Long expireDateAccessToken;
	private String accessToken;

	@Builder
	public Principal(Long memberId, String email, String loginId, Long expireDateAccessToken, String accessToken) {
		this.memberId = memberId;
		this.email = email;
		this.loginId = loginId;
		this.expireDateAccessToken = expireDateAccessToken;
		this.accessToken = accessToken;
	}

	public static Principal from(Claims claims, String accessToken) {
		PrincipalBuilder principal = Principal.builder();
		Optional.ofNullable(claims.get("memberId"))
			.ifPresent(memberId -> principal.memberId(Long.valueOf(memberId.toString())));
		Optional.ofNullable(claims.get("email"))
			.ifPresent(email -> principal.email((String)email));
		Optional.ofNullable(claims.get("loginId"))
			.ifPresent(loginId -> principal.loginId((String)loginId));
		Optional.ofNullable(claims.get("exp"))
			.ifPresent(expireDateAccessToken -> principal.expireDateAccessToken(
				Long.parseLong(expireDateAccessToken.toString())));
		Optional.ofNullable(accessToken)
			.ifPresent(token -> principal.accessToken(accessToken));
		return principal.build();
	}

	public static Principal from(Member member) {
		return Principal.builder()
			.memberId(member.getId())
			.email(member.getEmail())
			.loginId(member.getLoginId())
			.build();
	}

	public boolean isSeller(Member member) {
		return memberId.equals(member.getId());
	}

	public boolean isBuyer(Member member) {
		return memberId.equals(member.getId());
	}

	public String createItemViewKey(String key) {
		return loginId + "-" + key;
	}

	public String getChatPartnerName(Item item, ChatRoom chatRoom) {
		if (isSeller(item.getMember())) {
			return chatRoom.getBuyer().getLoginId();
		}
		return item.getMember().getLoginId();
	}

	@Override
	public String toString() {
		return String.format("%s, %s(id=%d, email=%s, loginId=%s)", "Principal", this.getClass().getSimpleName(),
			memberId, email, loginId);
	}

}
