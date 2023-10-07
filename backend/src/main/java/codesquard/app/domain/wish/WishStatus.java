package codesquard.app.domain.wish;

import java.util.Arrays;

import codesquard.app.api.errors.errorcode.SalesErrorCode;
import codesquard.app.api.errors.exception.BadRequestException;
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
			.orElseThrow(() -> new BadRequestException(SalesErrorCode.INVALID_SALES_PARAMETER));
	}
}
