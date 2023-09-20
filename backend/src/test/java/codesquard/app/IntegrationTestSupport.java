package codesquard.app;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import codesquard.app.api.category.CategoryQueryService;
import codesquard.app.api.item.ItemService;
import codesquard.app.api.member.MemberService;
import codesquard.app.api.membertown.MemberTownService;
import codesquard.app.api.oauth.OauthService;
import codesquard.app.api.region.RegionService;
import codesquard.app.api.sales.SalesItemService;
import codesquard.app.domain.category.CategoryRepository;
import codesquard.app.domain.chat.ChatLogRepository;
import codesquard.app.domain.chat.ChatRoomRepository;
import codesquard.app.domain.image.ImageRepository;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.member.MemberRepository;
import codesquard.app.domain.membertown.MemberTownRepository;
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
	protected RegionService regionService;

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
}
