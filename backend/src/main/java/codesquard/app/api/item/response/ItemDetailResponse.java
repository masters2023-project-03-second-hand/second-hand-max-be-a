package codesquard.app.api.item.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemDetailResponse {

	@JsonProperty("isSeller")
	private boolean isSeller;
	private List<String> imageUrls;
	private String seller;
	private String status;
	private String title;
	private String categoryName;
	private LocalDateTime createdAt;
	private String content;
	private int chatCount;
	private int wishCount;
	private Long viewCount;
	private Long price;

	@Builder(access = AccessLevel.PRIVATE)
	private ItemDetailResponse(boolean isSeller, List<String> imageUrls, String seller, String status, String title,
		String categoryName, LocalDateTime createdAt, String content, int chatCount, int wishCount, Long viewCount,
		Long price) {
		this.isSeller = isSeller;
		this.imageUrls = imageUrls;
		this.seller = seller;
		this.status = status;
		this.title = title;
		this.categoryName = categoryName;
		this.createdAt = createdAt;
		this.content = content;
		this.chatCount = chatCount;
		this.wishCount = wishCount;
		this.viewCount = viewCount;
		this.price = price;
	}

	public static ItemDetailResponse create(Item item, Member seller, Long loginMemberId, List<String> imageUrls,
		int chatCount, int wishCount) {
		boolean isSeller = seller.equalId(loginMemberId);
		return ItemDetailResponse.builder()
			.isSeller(isSeller)
			.imageUrls(imageUrls)
			.seller(seller.getLoginId())
			.status(item.getStatus().getStatus())
			.title(item.getTitle())
			.categoryName(item.getCategory().getName())
			.createdAt(item.getCreatedAt())
			.content(item.getContent())
			.chatCount(chatCount)
			.wishCount(wishCount)
			.viewCount(item.getViewCount())
			.price(item.getPrice())
			.build();
	}
}
