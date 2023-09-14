package codesquard.app.api.item;

import codesquard.app.domain.item.ItemStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ItemStatusModifyRequest {

	private ItemStatus status;
}
