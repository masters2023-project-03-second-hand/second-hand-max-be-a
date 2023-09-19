package codesquard.app.api.member;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.api.member.response.MemberProfileResponse;
import codesquard.app.api.response.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@PutMapping(value = "/{loginId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<MemberProfileResponse> modifyProfileImage(@RequestPart MultipartFile updateImageFile,
		@PathVariable String loginId) {
		return ApiResponse.ok("프로필 사진이 수정되었습니다.",
			memberService.modifyProfileImage(loginId, updateImageFile));
	}
}
