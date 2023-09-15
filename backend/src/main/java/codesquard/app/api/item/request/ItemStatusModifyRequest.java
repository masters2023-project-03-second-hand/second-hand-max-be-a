package codesquard.app.api.item.request;

import codesquard.app.domain.item.ItemStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ItemStatusModifyRequest {

	private ItemStatus status;
}
