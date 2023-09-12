package codesquard.app.domain.wish;

import java.util.Arrays;

import codesquard.app.api.errors.errorcode.WishErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.wishitem.WishItemService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum WishStatus {

	YES("yes") {
		@Override
		public void doMethod(WishItemService wishItemService, Long itemId, Long memberId) {
			wishItemService.register(itemId, memberId);
		}
	},
	NO("no") {
		@Override
		public void doMethod(WishItemService wishItemService, Long itemId, Long memberId) {
			wishItemService.cancel(itemId);
		}
	};

	private final String status;

	public static WishStatus of(String stringStatus) {
		return Arrays.stream(WishStatus.values())
			.filter(wishStatus -> wishStatus.getStatus().equals(stringStatus.toLowerCase()))
			.findFirst()
			.orElseThrow(() -> new RestApiException(WishErrorCode.INVALID_PARAMETER));
	}

	public abstract void doMethod(WishItemService wishItemService, Long itemId, Long memberId);
}
