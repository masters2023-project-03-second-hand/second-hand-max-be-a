package codesquard.app.api.response;

import java.time.LocalDateTime;

import codesquard.app.domain.item.ItemStatus;
import lombok.Getter;

@Getter
public class ItemResponse {

	private Long itemId;
	private String thumbnailUrl;
	private String title;
	private String region;
	private LocalDateTime createdAt;
	private Integer price;
	private ItemStatus status;
	private Long chatCount;
	private Long wishCount;
}
