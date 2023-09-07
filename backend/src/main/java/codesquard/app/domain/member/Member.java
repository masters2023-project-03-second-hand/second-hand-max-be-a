package codesquard.app.domain.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import codesquard.app.domain.chat.ChatRoom;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.membertown.MemberTown;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	@OneToMany(cascade = CascadeType.ALL)
	private List<MemberTown> towns = new ArrayList<>(); // 동네

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<Item> items = new ArrayList<>(); // 회원이 등록한 상품

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<ChatRoom> chatRooms = new ArrayList<>(); // 채팅방

	public Member(Long id) {
		this.id = id;
	}

	private Member(String avatarUrl, String email, String loginId) {
		this.avatarUrl = avatarUrl;
		this.email = email;
		this.loginId = loginId;
		this.towns = new ArrayList<>();
	}

	public static Member create(String avatarUrl, String email, String loginId) {
		return new Member(avatarUrl, email, loginId);
	}

	public void addItem(Item item) {
		if (item != null && item.getMember() != this) {
			item.setMember(this);
		}
		if (item != null && !items.contains(item)) {
			items.add(item);
		}
	}

	public void addMemberTown(MemberTown town) {
		if (town != null && !towns.contains(town)) {
			towns.add(town);
		}
	}

	public void addChatRoom(ChatRoom chatRoom) {
		if (chatRoom != null && !chatRooms.contains(chatRoom)) {
			chatRooms.add(chatRoom);
		}
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

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
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
