package codesquard.app.api.item.response;

import java.time.LocalDateTime;

import codesquard.app.domain.item.ItemStatus;
import lombok.Getter;

@Getter
public class ItemResponse {

	private Long itemId;
	private String thumbnailUrl;
	private String title;
	private String tradingRegion;
	private LocalDateTime createdAt;
	private Long price;
	private ItemStatus status;
	private Long chatCount;
	private Long wishCount;
	private String sellerId;
}
