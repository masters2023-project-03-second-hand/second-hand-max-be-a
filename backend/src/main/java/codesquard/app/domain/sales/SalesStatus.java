package codesquard.app.domain.sales;

import java.util.Arrays;

import codesquard.app.api.errors.errorcode.SalesErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SalesStatus {
	All("all"),
	ON_SALE("on_sale"),
	SOLD_OUT("sold_out");

	private final String status;

	public static SalesStatus of(String stringStatus) {
		return Arrays.stream(SalesStatus.values())
			.filter(salesStatus -> salesStatus.getStatus().equals(stringStatus.toLowerCase()))
			.findFirst()
			.orElseThrow(() -> new RestApiException(SalesErrorCode.INVALID_PARAMETER));
	}
}
