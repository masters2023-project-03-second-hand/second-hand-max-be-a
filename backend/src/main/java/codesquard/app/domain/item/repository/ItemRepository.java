package codesquard.app.domain.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import codesquard.app.domain.item.entity.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
