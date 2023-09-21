package codesquard.app.api.chat;

import static codesquard.app.MemberTestSupport.*;
import static codesquard.app.MemberTownTestSupport.*;
import static codesquard.app.domain.item.ItemStatus.*;
import static java.time.LocalDateTime.*;
import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import codesquard.app.CategoryTestSupport;
import codesquard.app.RegionTestSupport;
import codesquard.app.api.chat.request.ChatLogSendRequest;
import codesquard.app.api.chat.response.ChatLogSendResponse;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.category.CategoryRepository;
import codesquard.app.domain.chat.ChatLogRepository;
import codesquard.app.domain.chat.ChatRoom;
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
class ChatLogServiceTest {

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
	private ChatLogRepository chatLogRepository;

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@Autowired
	private ChatLogService chatLogService;

	@Autowired
	private ObjectMapper objectMapper;

	@AfterEach
	void tearDown() {
		chatLogRepository.deleteAllInBatch();
		chatRoomRepository.deleteAllInBatch();
		imageRepository.deleteAllInBatch();
		itemRepository.deleteAllInBatch();
		memberTownRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
		regionRepository.deleteAllInBatch();
		categoryRepository.deleteAllInBatch();
	}

	@DisplayName("채팅 메시지를 전송한다")
	@Test
	public void sendMessage() throws JsonProcessingException {
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

		ChatRoom chatRoom = chatRoomRepository.save(new ChatRoom(now(), buyer, item));

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("message", "롤러블레이드 사고 싶습니다.");
		ChatLogSendRequest request = objectMapper.readValue(objectMapper.writeValueAsString(requestBody),
			ChatLogSendRequest.class);

		// when
		ChatLogSendResponse response = chatLogService.sendMessage(request, chatRoom.getId(),
			Principal.from(buyer));

		// then
		assertThat(response)
			.extracting("message", "sender", "receiver")
			.containsExactlyInAnyOrder("롤러블레이드 사고 싶습니다.", "bruni", "23Yong");
	}
}
