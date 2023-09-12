package codesquard.app.api.membertown;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import codesquard.app.api.membertown.request.MemberTownAddRequest;
import codesquard.app.api.membertown.request.MemberTownRemoveRequest;
import codesquard.app.api.response.ApiResponse;
import codesquard.app.domain.oauth.support.AuthPrincipal;
import codesquard.app.domain.oauth.support.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping(path = "/api/regions")
@RequiredArgsConstructor
@RestController
public class MemberTownRestController {

	private final MemberTownService memberTownService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<Void> addMemberTown(@AuthPrincipal Principal principal,
		@Valid @RequestBody MemberTownAddRequest request) {
		memberTownService.addMemberTown(principal, request);
		return ApiResponse.created("동네 추가에 성공하였습니다.", null);
	}

	@DeleteMapping
	public ApiResponse<Void> removeMemberTown(@AuthPrincipal Principal principal,
		@Valid @RequestBody MemberTownRemoveRequest request) {
		memberTownService.removeMemberTown(principal, request);
		return ApiResponse.ok("동네 삭제에 성공하였습니다.", null);
	}
}
