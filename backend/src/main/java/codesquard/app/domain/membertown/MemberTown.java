package codesquard.app.domain.membertown;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne
	@JoinColumn(name = "region_id")
	private Region region;

	private MemberTown(String name, Member member, Region region) {
		this.name = name;
		this.member = member;
		this.region = region;
	}

	public static MemberTown create(String name, Member member, Region region) {
		return new MemberTown(name, member, region);
	}

	public static List<MemberTown> create(List<Region> regions, Member member) {
		List<MemberTown> memberTowns = new ArrayList<>();
		for (Region region : regions) {
			String name = convertShortAddressName(region.getName());
			memberTowns.add(create(name, member, region));
		}
		return memberTowns;
	}

	private static String convertShortAddressName(String fullAddressName) {
		final String space = " ";
		return Arrays.stream(fullAddressName.split(space))
			.skip(2)
			.collect(Collectors.joining(space));
	}

	@Override
	public String toString() {
		return String.format("%s, %s(id=%d, name=%s)", "회원동네", this.getClass().getSimpleName(), id, name);
	}
}
