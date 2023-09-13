package codesquard.app.domain.sales;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SalesStatus {
	All("all"),
	ON_SALE("on_sale"),
	SOLD_OUT("sold_out");

	private final String status;

}
