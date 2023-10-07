package codesquard.app.api.member;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.api.member.response.MemberProfileResponse;
import codesquard.app.api.membertown.MemberTownService;
import codesquard.app.api.membertown.response.MemberTownListResponse;
import codesquard.app.api.response.ApiResponse;
import codesquard.app.api.success.successcode.MemberSuccessCode;
import codesquard.app.domain.oauth.support.AuthPrincipal;
import codesquard.app.domain.oauth.support.Principal;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final MemberTownService memberTownService;

	@PutMapping(value = "/{loginId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<MemberProfileResponse> modifyProfileImage(@RequestPart MultipartFile updateImageFile,
		@PathVariable String loginId) {
		return ApiResponse.success(MemberSuccessCode.OK_MODIFIED_PROFILE_IMAGE,
			memberService.modifyProfileImage(loginId, updateImageFile));
	}

	@GetMapping(value = "/regions")
	public ApiResponse<MemberTownListResponse> readAllMemberTowns(@AuthPrincipal Principal principal) {
		return ApiResponse.success(MemberSuccessCode.OK_MEMBER_TOWNS, memberTownService.readAll(principal));
	}
}
