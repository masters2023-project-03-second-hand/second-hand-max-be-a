package codesquard.app.domain.region;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {

	List<Region> findAllByNameIn(List<String> names);
}
