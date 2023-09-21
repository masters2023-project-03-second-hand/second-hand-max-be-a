package codesquard.app.api.chat;

import static codesquard.app.MemberTestSupport.*;
import static codesquard.app.MemberTownTestSupport.*;
import static codesquard.app.domain.item.ItemStatus.*;
import static java.time.LocalDateTime.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import codesquard.app.CategoryTestSupport;
import codesquard.app.RegionTestSupport;
import codesquard.app.api.chat.response.ChatRoomCreateResponse;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.category.CategoryRepository;
import codesquard.app.domain.chat.ChatRoomRepository;
import codesquard.app.domain.image.Image;
import codesquard.app.domain.image.ImageRepository;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.member.MemberRepository;
import codesquard.app.domain.membertown.MemberTownRepository;
import codesquard.app.domain.oauth.support.Principal;
import codesquard.app.domain.region.Region;
import codesquard.app.domain.region.RegionRepository;

@ActiveProfiles("test")
@SpringBootTest
class ChatRoomServiceTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MemberTownRepository memberTownRepository;

	@Autowired
	private RegionRepository regionRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@Autowired
	private ChatRoomService chatRoomService;

	@BeforeEach
	void cleanup() {
		memberRepository.deleteAllInBatch();
	}

	@AfterEach
	void tearDown() {
		chatRoomRepository.deleteAllInBatch();
		imageRepository.deleteAllInBatch();
		itemRepository.deleteAllInBatch();
		memberTownRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
		regionRepository.deleteAllInBatch();
		categoryRepository.deleteAllInBatch();
	}

	@DisplayName("구매자가 한 상품에 대한 채팅방을 생성한다")
	@Test
	public void createChatRoom() {
		// given
		Member seller = memberRepository.save(createMember("avatarUrlValue1", "23Yong@gmail.com", "23Yong"));
		Member buyer = memberRepository.save(createMember("avatarUrlValue2", "bruni@gmail.com", "bruni"));

		Region region = regionRepository.save(RegionTestSupport.createRegion("서울 종로구 청운동"));

		memberTownRepository.saveAll(List.of(
			createMemberTown(seller, region, true),
			createMemberTown(buyer, region, true)));

		Category category = categoryRepository.save(CategoryTestSupport.findByName("스포츠/레저"));

		Item item = Item.builder()
			.title("빈티지 롤러 블레이드")
			.content("어린시절 추억의향수를 불러 일으키는 롤러 스케이트입니다.")
			.price(200000L)
			.status(ON_SALE)
			.region("가락동")
			.createdAt(now())
			.wishCount(0L)
			.viewCount(0L)
			.chatCount(0L)
			.member(seller)
			.category(category)
			.build();
		Item saveItem = itemRepository.save(item);
		List<Image> images = List.of(
			new Image("imageUrlValue1", saveItem, true),
			new Image("imageUrlValue2", saveItem, false));
		imageRepository.saveAll(images);

		// when
		ChatRoomCreateResponse response = chatRoomService.createChatRoom(saveItem.getId(), Principal.from(buyer));

		// then
		assertAll(() -> {
			assertThat(response)
				.extracting("id")
				.isNotNull();
		});
	}
}
