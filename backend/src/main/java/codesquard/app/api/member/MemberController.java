package codesquard.app.api.member;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.api.response.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@PutMapping(value = "/{loginId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<Void>> modifyProfileImage(@RequestPart MultipartFile updateImageFile,
		@PathVariable String loginId) {
		memberService.modifyProfileImage(loginId, updateImageFile);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok("프로필 사진이 수정되었습니다.", null));
	}
}
