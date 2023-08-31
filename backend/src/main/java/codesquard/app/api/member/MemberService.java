package codesquard.app.api.member;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.api.errors.errorcode.MemberErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.image.ImageService;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final ImageService imageService;

	@Transactional
	public void modifyProfileImage(String loginId, MultipartFile updateImageFile) {
		Member member = memberRepository.findMemberByLoginId(loginId)
			.orElseThrow(() -> new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER));
		imageService.deleteImage(member.getAvatarUrl());
		member.setAvatarUrl(imageService.uploadImage(updateImageFile));
	}
}