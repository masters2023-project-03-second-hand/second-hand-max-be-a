package codesquard.app.api.item;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import codesquard.app.api.errors.errorcode.ImageErrorCode;
import codesquard.app.api.errors.errorcode.ItemErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.domain.image.Image;
import codesquard.app.domain.image.ImageRepository;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.oauth.support.Principal;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ItemValidator {

	private final ImageRepository imageRepository;
	private final ItemRepository itemRepository;

	public void validateContainsImage(List<String> imageUrls, Long itemId) {
		List<String> findImageUrls = imageRepository.findAllByItemId(itemId).stream()
			.map(Image::getImageUrl)
			.collect(Collectors.toUnmodifiableList());
		boolean allMatch = new HashSet<>(findImageUrls).containsAll(imageUrls);
		if (!allMatch) {
			throw new RestApiException(ImageErrorCode.NOT_FOUND_IMAGE_URL);
		}
	}

	public void validateAuthorization(Principal writer, Long itemId) {
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new RestApiException(ItemErrorCode.ITEM_NOT_FOUND));
		if (!Objects.equals(item.getMember().getId(), writer.getMemberId())) {
			throw new RestApiException(ItemErrorCode.ITEM_FORBIDDEN);
		}
	}
}
