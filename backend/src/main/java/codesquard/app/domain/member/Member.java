package codesquard.app.domain.member;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Entity
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 회원 등록번호
	private String avatarUrl; // 프로필 사진
	private String email; // 소셜 사용자의 이메일
	@Column(name = "login_id", nullable = false, unique = true)
	private String loginId; // 닉네임

	public Member(Long id) {
		this.id = id;
	}

	private Member(String avatarUrl, String email, String loginId) {
		this.avatarUrl = avatarUrl;
		this.email = email;
		this.loginId = loginId;
	}

	public static Member create(String avatarUrl, String email, String loginId) {
		return new Member(avatarUrl, email, loginId);
	}

	public void changeAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String createRedisKey() {
		return "RT:" + email;
	}

	public Map<String, Object> createClaims() {
		Map<String, Object> claims = new HashMap<>();
		claims.put("memberId", id);
		claims.put("email", email);
		claims.put("loginId", loginId);
		return claims;
	}

	public boolean equalId(Long memberId) {
		return Objects.equals(id, memberId);
	}

	@Override
	public String toString() {
		return String.format("%s, %s(id=%d, email=%s, loginId=%s)", "회원", this.getClass().getSimpleName(), id, email,
			loginId);
	}

}
