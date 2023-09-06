package codesquard.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import codesquard.app.api.category.CategoryQueryService;
import codesquard.app.api.category.CategoryQueryService;
import codesquard.app.api.item.ItemQueryService;
import codesquard.app.api.oauth.OauthService;
import codesquard.app.domain.category.CategoryRepository;
import codesquard.app.domain.chat.ChatLogRepository;
import codesquard.app.domain.chat.ChatRoomRepository;
import codesquard.app.domain.image.ImageRepository;
import codesquard.app.domain.interest.InterestRepository;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.category.CategoryRepository;
import codesquard.app.domain.member.MemberRepository;
import codesquard.app.domain.membertown.MemberTownRepository;

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
	protected InterestRepository interestRepository;

	@Autowired
	protected ChatLogRepository chatLogRepository;

	@Autowired
	protected ChatRoomRepository chatRoomRepository;
}
