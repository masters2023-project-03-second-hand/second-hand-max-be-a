package codesquard.app.api.member;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.api.errors.errorcode.ErrorCode;
import codesquard.app.api.errors.exception.NotFoundResourceException;
import codesquard.app.api.image.ImageService;
import codesquard.app.api.member.response.MemberProfileResponse;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final ImageService imageService;

	@Transactional
	public MemberProfileResponse modifyProfileImage(String loginId, MultipartFile updateImageFile) {
		Member member = memberRepository.findMemberByLoginId(loginId)
			.orElseThrow(() -> new NotFoundResourceException(ErrorCode.NOT_FOUND_MEMBER));
		imageService.deleteImage(member.getAvatarUrl());
		String avatarUrl = imageService.uploadImage(updateImageFile);
		member.changeAvatarUrl(avatarUrl);
		return new MemberProfileResponse(avatarUrl);
	}

	public Member findMemberByLoginId(String loginId) {
		return memberRepository.findMemberByLoginId(loginId)
			.orElseThrow(() -> new NotFoundResourceException(ErrorCode.NOT_FOUND_MEMBER));
	}
}
