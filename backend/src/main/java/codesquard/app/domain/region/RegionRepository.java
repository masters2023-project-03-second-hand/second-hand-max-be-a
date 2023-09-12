package codesquard.app.domain.region;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {
	boolean existsRegionByName(String name);
}
