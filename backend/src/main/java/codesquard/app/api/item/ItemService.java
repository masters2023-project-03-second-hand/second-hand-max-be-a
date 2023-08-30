package codesquard.app.api.item;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.api.image.ImageService;
import codesquard.app.domain.image.Image;
import codesquard.app.domain.image.ImageRepository;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.member.Member;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {

	private final ItemRepository itemRepository;
	private final ImageRepository imageRepository;
	private final ImageService imageService;

	@Transactional
	public void register(ItemRegisterRequest request, List<MultipartFile> itemImage, Long memberId) {
		List<String> serverFileUrls = imageService.uploadImages(itemImage);
		List<Image> images = new ArrayList<>();

		Item item = itemRepository.save(Item.toEntity(request, new Member(memberId)));

		for (String serverFileUrl : serverFileUrls) {
			images.add(new Image(item, serverFileUrl));
		}

		imageRepository.saveAll(images);
	}
}
