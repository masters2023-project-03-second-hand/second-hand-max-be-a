package codesquard.app.api.region.request;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegionSelectionRequest {
	
	@NotNull(message = "지역 등록번호는 필수 정보입니다.")
	private Long selectedAddressId;
}
