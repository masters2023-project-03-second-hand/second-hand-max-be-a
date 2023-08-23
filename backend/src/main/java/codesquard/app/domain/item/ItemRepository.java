package codesquard.app.domain.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import codesquard.app.domain.item.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
