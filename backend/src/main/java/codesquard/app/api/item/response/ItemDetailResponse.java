package codesquard.app.api.item.response;

import java.time.LocalDateTime;
import java.util.List;

import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemDetailResponse {
	
	private Boolean isSeller;
	private List<String> imageUrls;
	private String seller;
	private String status;
	private String title;
	private String categoryName;
	private LocalDateTime createdAt;
	private String content;
	private Long chatCount;
	private Long wishCount;
	private Long viewCount;
	private Long price;

	@Builder(access = AccessLevel.PRIVATE)
	private ItemDetailResponse(boolean isSeller, List<String> imageUrls, String seller, String status, String title,
		String categoryName, LocalDateTime createdAt, String content, Long chatCount, Long wishCount, Long viewCount,
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

	public static ItemDetailResponse of(Item item, Long loginMemberId, List<String> imageUrls) {
		Member seller = item.getMember();
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
			.chatCount(item.getChatCount())
			.wishCount(item.getWishCount())
			.viewCount(item.getViewCount())
			.price(item.getPrice())
			.build();
	}

	@Override
	public String toString() {
		return String.format(
			"ItemDetailResponse{isSeller=%s, imageUrls=%s, seller='%s', status='%s', title='%s', categoryName='%s', createdAt=%s, content='%s', chatCount=%d, wishCount=%d, viewCount=%d, price=%d}",
			isSeller, imageUrls, seller, status, title, categoryName, createdAt, content, chatCount, wishCount,
			viewCount, price);
	}
}
