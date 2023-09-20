package codesquard.app.api.item.request;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import codesquard.app.api.converter.ItemRequestConverter;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemModifyRequest {
	@NotBlank(message = "제목은 필수 정보입니다.")
	private String title;
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
	private String thumnailImage;

	public Item toEntity() {
		return Item.builder()
			.title(title)
			.price(price)
			.content(content)
			.region(region)
			.status(status)
			.build();
	}

	@Override
	public String toString() {
		return String.format("%s, %s(title=%s, price=%d, region=%s, itemStatus=%s, categoryId=%d)",
			"상품 수정 요청", this.getClass().getSimpleName(), title, price, region, status, categoryId);
	}
}
