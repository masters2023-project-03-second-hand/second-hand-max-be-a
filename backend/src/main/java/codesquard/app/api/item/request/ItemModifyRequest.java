package codesquard.app.api.item.request;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import codesquard.app.api.converter.ItemRequestConverter;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemModifyRequest {
	@NotBlank(message = "제목은 필수 정보입니다.")
	private String title;
	@PositiveOrZero(message = "가격은 음수이면 안됩니다.")
	private Long price;
	private String content;
	@NotBlank(message = "동네는 필수 정보입니다.")
	private String region;
	@JsonDeserialize(converter = ItemRequestConverter.class)
	private ItemStatus status;
	@Positive(message = "카테고리 등록번호는 양수여야 합니다.")
	private Long categoryId;
	@NotBlank(message = "카테고리명은 필수 정보입니다.")
	private String categoryName;
	private List<String> deleteImageUrls;

	public static ItemModifyRequest create(String title, Long price, String content, String region, ItemStatus status,
		Long categoryId, String categoryName, List<String> deleteImageUrls) {
		return new ItemModifyRequest(title, price, content, region, status, categoryId, categoryName, deleteImageUrls);
	}

	public Item toEntity(String thumbnailUrl) {
		return Item.builder()
			.title(title)
			.price(price)
			.content(content)
			.region(region)
			.status(status)
			.thumbnailUrl(thumbnailUrl)
			.build();
	}

	@Override
	public String toString() {
		return String.format("%s, %s(title=%s, price=%d, region=%s, itemStatus=%s, categoryId=%d)",
			"상품 수정 요청", this.getClass().getSimpleName(), title, price, region, status, categoryId);
	}
}
