package codesquard.app.domain.item.entity;

import java.time.LocalDateTime;

public class Item {
	private Long id;
	private String title;
	private String content;
	private int price;
	private String status;
	private String region;
	private LocalDateTime createdAt;

	private String imageUrl;
}
