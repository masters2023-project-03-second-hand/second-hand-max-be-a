package codesquard.app.domain.wish;

import java.util.Arrays;

import codesquard.app.api.errors.errorcode.WishErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum WishStatus {

	YES("yes"),
	NO("no");

	private final String status;

	public static WishStatus of(String stringStatus) {
		return Arrays.stream(WishStatus.values())
			.filter(wishStatus -> wishStatus.getStatus().equals(stringStatus.toLowerCase()))
			.findFirst()
			.orElseThrow(() -> new RestApiException(WishErrorCode.INVALID_PARAMETER));
	}
}
