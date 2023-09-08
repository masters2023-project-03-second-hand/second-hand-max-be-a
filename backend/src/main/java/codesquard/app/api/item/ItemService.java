package codesquard.app.api.item;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.api.image.ImageService;
import codesquard.app.api.response.ItemResponse;
import codesquard.app.api.response.ItemResponses;
import codesquard.app.domain.image.Image;
import codesquard.app.domain.image.ImageRepository;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemPaginationRepository;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.member.Member;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {

	private final ItemRepository itemRepository;
	private final ImageRepository imageRepository;
	private final ImageService imageService;
	private final ItemPaginationRepository itemPaginationRepository;

	@Transactional
	public void register(ItemRegisterRequest request, List<MultipartFile> itemImage, Long memberId) {
		List<String> serverFileUrls = imageService.uploadImages(itemImage);
		List<Image> images = new ArrayList<>();

		Item item = itemRepository.save(request.toEntity(new Member(memberId), serverFileUrls.get(0)));

		for (String serverFileUrl : serverFileUrls) {
			images.add(new Image(item, serverFileUrl));
		}

		imageRepository.saveAll(images);
	}

	@Transactional
	public ItemResponses findAll(String region, int size, Long cursor, Long categoryId) {
		Slice<ItemResponse> itemResponses = itemPaginationRepository.findByIdAndRegion(cursor, region, size,
			categoryId);

		List<ItemResponse> contents = itemResponses.getContent();
		boolean hasNext = itemResponses.hasNext();
		Long nextCursor = null;
		if (hasNext) {
			nextCursor = contents.get(contents.size() - 1).getItemId();
		}
		return new ItemResponses(contents, hasNext, nextCursor);
	}
}
