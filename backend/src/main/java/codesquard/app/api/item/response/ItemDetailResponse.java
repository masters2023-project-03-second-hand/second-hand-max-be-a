package codesquard.app.api.item.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import codesquard.app.domain.image.Image;
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
	private int viewCount;
	private int price;

	@Builder(access = AccessLevel.PRIVATE)
	public ItemDetailResponse(boolean isSeller, List<String> imageUrls, String seller,
		String status,
		String title, String categoryName, LocalDateTime createdAt, String content,
		int chatCount, int wishCount, int viewCount, int price) {
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

	public static ItemDetailResponse createWithSellerResponse(Item item, Member seller) {
		List<String> imageUrls = item.getImages().stream()
			.map(Image::getImageUrl)
			.collect(Collectors.toUnmodifiableList());

		return ItemDetailResponse.builder()
			.isSeller(true)
			.imageUrls(imageUrls)
			.seller(seller.getLoginId())
			.status(item.getStatus().getStatus())
			.title(item.getTitle())
			.categoryName(item.getCategory().getName())
			.createdAt(item.getCreatedAt())
			.content(item.getContent())
			.chatCount(item.getTotalChatLogCount())
			.wishCount(item.getInterests().size())
			.viewCount(item.getViewCount().intValue())
			.price(item.getPrice().intValue())
			.build();
	}
}