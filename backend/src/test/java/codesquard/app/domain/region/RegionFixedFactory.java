package codesquard.app.domain.region;

import java.util.ArrayList;
import java.util.List;

public class RegionFixedFactory {
	public static List<Region> createFixedRegions() {
		List<Region> regions = new ArrayList<>();
		regions.add(Region.create("경기 부천시 원미동"));
		regions.add(Region.create("경기 부천시 심곡동"));
		regions.add(Region.create("경기 부천시 춘의동"));
		regions.add(Region.create("경기 부천시 도당동"));
		regions.add(Region.create("경기 부천시 약대동"));
		regions.add(Region.create("경기 부천시 소사동"));
		regions.add(Region.create("경기 부천시 역곡동"));
		regions.add(Region.create("경기 부천시 중동"));
		regions.add(Region.create("경기 부천시 상동"));
		regions.add(Region.create("경기 부천시 소사본동"));
		regions.add(Region.create("경기 부천시 심곡본동"));
		regions.add(Region.create("경기 부천시 범박동"));
		regions.add(Region.create("경기 부천시 괴안동"));
		regions.add(Region.create("경기 부천시 송내동"));
		regions.add(Region.create("경기 부천시 옥길동"));
		regions.add(Region.create("경기 부천시 계수동"));
		regions.add(Region.create("경기 부천시 오정동"));
		regions.add(Region.create("경기 부천시 여월동"));
		regions.add(Region.create("경기 부천시 작동"));
		regions.add(Region.create("경기 부천시 원종동"));
		return regions;
	}
}
