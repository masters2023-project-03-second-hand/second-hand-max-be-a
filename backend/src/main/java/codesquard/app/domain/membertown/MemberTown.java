package codesquard.app.domain.membertown;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ColumnDefault;

import codesquard.app.domain.member.Member;
import codesquard.app.domain.region.Region;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberTown {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 회원의 동네 등록번호
	private String name; // 동네 이름
	@Column(name = "is_selected", nullable = false)
	@ColumnDefault("false")
	private boolean isSelected; // 동네 선택 여부

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne
	@JoinColumn(name = "region_id")
	private Region region;

	private MemberTown(String name, Member member, Region region, boolean isSelected) {
		this.name = name;
		this.member = member;
		this.region = region;
		this.isSelected = isSelected;
	}

	public static List<MemberTown> createMemberTowns(List<Region> regions, Member member) {
		List<MemberTown> memberTowns = new ArrayList<>();
		for (Region region : regions) {
			memberTowns.add(new MemberTown(region.getShortAddress(), member, region, false));
		}
		return memberTowns;
	}

	public static MemberTown selectedMemberTown(Region region, Member member) {
		return new MemberTown(region.getShortAddress(), member, region, true);
	}

	public static MemberTown notSelectedMemberTown(Region region, Member member) {
		return new MemberTown(region.getShortAddress(), member, region, false);
	}

	@Override
	public String toString() {
		return String.format("%s, %s(id=%d, name=%s, isSelected=%s)", "회원동네", this.getClass().getSimpleName(), id, name,
			isSelected);
	}
}
