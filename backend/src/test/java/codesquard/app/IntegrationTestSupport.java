package codesquard.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import codesquard.app.api.oauth.OauthService;
import codesquard.app.domain.member.MemberRepository;

@SpringBootTest
public abstract class IntegrationTestSupport {

	@Autowired
	protected OauthService oauthService;

	@Autowired
	protected MemberRepository memberRepository;
}
