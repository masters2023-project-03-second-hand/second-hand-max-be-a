package codesquard.app.domain.region;

import static codesquard.app.domain.region.QRegion.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class RegionPaginationRepository {

	private final JPAQueryFactory queryFactory;

	public Slice<Region> searchBySlice(Long lastRegionId, String regionName, Pageable pageable) {
		List<Region> regions = queryFactory.selectFrom(region)
			.where(
				// no-offset 페이징 처리
				lessThanRegionId(lastRegionId),
				// 동네 이름이 포함된 지역 검색
				likeName(regionName)
			)
			.orderBy(region.id.desc())
			.limit(pageable.getPageSize() + 1)
			.fetch();
		// 무한 스크롤 처리
		return checkLastPage(pageable, regions);
	}

	private BooleanExpression lessThanRegionId(Long regionId) {
		if (regionId == null) {
			return null;
		}
		return region.id.lt(regionId);
	}

	private BooleanExpression likeName(String name) {
		if (name == null) {
			return null;
		}
		return region.name.like("%" + name + "%");
	}

	private Slice<Region> checkLastPage(Pageable pageable, List<Region> regions) {
		boolean hasNext = false;

		// 뒷 페이지가 더 있는지 확인
		if (regions.size() > pageable.getPageSize()) {
			hasNext = true;
			// LIMIT로 인해 한개 더 조회한 데이터를 제거
			regions.remove(pageable.getPageSize());
		}
		return new SliceImpl<>(regions, pageable, hasNext);
	}
}
