package codesquard.app.domain.item;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import codesquard.app.api.errors.errorcode.ItemErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ItemStatus {

	ON_SALE("판매중"),
	SOLD_OUT("판매완료"),
	RESERVED("예약중");

	@JsonValue
	private final String status;

	@JsonCreator
	public static ItemStatus of(String status) {
		return Arrays.stream(ItemStatus.values())
			.filter(itemStatus -> itemStatus.getStatus().equals(status))
			.findFirst().orElseThrow(() -> new RestApiException(ItemErrorCode.INVALID_STATUS));
	}
}
