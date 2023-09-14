package codesquard.app;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import codesquard.app.api.category.CategoryQueryService;
import codesquard.app.api.item.ItemQueryService;
import codesquard.app.api.item.ItemService;
import codesquard.app.api.member.MemberService;
import codesquard.app.api.membertown.MemberTownService;
import codesquard.app.api.oauth.OauthService;
import codesquard.app.api.region.RegionQueryService;
import codesquard.app.api.sales.SalesItemService;
import codesquard.app.domain.category.CategoryRepository;
import codesquard.app.domain.chat.ChatLogRepository;
import codesquard.app.domain.chat.ChatRoomRepository;
import codesquard.app.domain.image.ImageRepository;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.member.MemberRepository;
import codesquard.app.domain.membertown.MemberTownRepository;
import codesquard.app.domain.region.Region;
import codesquard.app.domain.region.RegionPaginationRepository;
import codesquard.app.domain.region.RegionRepository;
import codesquard.app.domain.wish.WishRepository;
import codesquard.support.SupportRepository;

@SpringBootTest
public abstract class IntegrationTestSupport {

	@Autowired
	protected OauthService oauthService;

	@Autowired
	protected MemberRepository memberRepository;

	@Autowired
	protected MemberTownRepository memberTownRepository;

	@Autowired
	protected CategoryQueryService categoryQueryService;

	@Autowired
	protected CategoryRepository categoryRepository;

	@Autowired
	protected ItemRepository itemRepository;

	@Autowired
	protected ItemQueryService itemQueryService;

	@Autowired
	protected ImageRepository imageRepository;

	@Autowired
	protected WishRepository wishRepository;

	@Autowired
	protected ChatLogRepository chatLogRepository;

	@Autowired
	protected ChatRoomRepository chatRoomRepository;

	@Autowired
	protected RegionPaginationRepository regionPaginationRepository;

	@Autowired
	protected RegionRepository regionRepository;

	@Autowired
	protected RegionQueryService regionQueryService;

	@Autowired
	protected MemberService memberService;

	@Autowired
	protected MemberTownService memberTownService;

	@Autowired
	protected ItemService itemService;

	@Autowired
	protected SalesItemService salesItemService;

	@Autowired
	protected SupportRepository supportRepository;

	@AfterEach
	void cleanup() {
		chatLogRepository.deleteAllInBatch();
		chatRoomRepository.deleteAllInBatch();
		wishRepository.deleteAllInBatch();
		imageRepository.deleteAllInBatch();
		itemRepository.deleteAllInBatch();
		categoryRepository.deleteAllInBatch();
		memberTownRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}

	protected List<Long> getAddressIds(String name) {
		List<Region> regions = regionRepository.findAllByNameIn(List.of(name));
		return regions.stream()
			.map(Region::getId)
			.collect(Collectors.toUnmodifiableList());
	}

	protected List<Region> getRegions(List<String> names) {
		return regionRepository.findAllByNameIn(names);
	}

	protected Region getRegion(String name) {
		return regionRepository.findAllByNameIn(List.of(name))
			.stream()
			.findAny()
			.orElseThrow();
	}
}
