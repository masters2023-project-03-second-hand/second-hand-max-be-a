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
				likeRegionName(regionName)
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

	private BooleanExpression likeRegionName(String name) {
		if (name == null) {
			return null;
		}
		return region.name.like("%" + name + "%");
	}

	private Slice<Region> checkLastPage(Pageable pageable, List<Region> regions) {
		boolean hasNext = false;

		// 조회된 동네의 개수가 요청한 페이지의 개수보다 크면 뒤에 더 있다는 것을 의미합니다.
		if (regions.size() > pageable.getPageSize()) {
			hasNext = true;
			// 조회된 동네의 개수는 요청한 페이지의 개수보다 1개더 많이 조회하였기 때문에 마지막 동네 데이터를 제거합니다.
			regions.remove(pageable.getPageSize());
		}
		return new SliceImpl<>(regions, pageable, hasNext);
	}
}
