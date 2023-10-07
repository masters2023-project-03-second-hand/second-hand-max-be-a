package codesquard.app.api.membertown;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import codesquard.app.api.membertown.request.MemberTownAddRequest;
import codesquard.app.api.membertown.request.MemberTownRemoveRequest;
import codesquard.app.api.region.request.RegionSelectionRequest;
import codesquard.app.api.response.ApiResponse;
import codesquard.app.api.success.successcode.MemberTownSuccessCode;
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
		return ApiResponse.success(MemberTownSuccessCode.CREATED_MEMBER_TOWN);
	}

	@DeleteMapping
	public ApiResponse<Void> removeMemberTown(@AuthPrincipal Principal principal,
		@Valid @RequestBody MemberTownRemoveRequest request) {
		memberTownService.removeMemberTown(principal, request);
		return ApiResponse.success(MemberTownSuccessCode.OK_DELETED_MEMBER_TOWN);
	}

	@PutMapping
	public ApiResponse<Void> selectRegion(
		@Valid @RequestBody RegionSelectionRequest request,
		@AuthPrincipal Principal principal) {
		memberTownService.selectRegion(request, principal);
		return ApiResponse.success(MemberTownSuccessCode.OK_SELECTED_MEMBER_TOWN);
	}
}
