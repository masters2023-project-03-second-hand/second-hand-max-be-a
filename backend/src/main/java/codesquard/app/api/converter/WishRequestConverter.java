package codesquard.app.api.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import codesquard.app.domain.wish.WishStatus;

@Component
public class WishRequestConverter implements Converter<String, WishStatus> {
	@Override
	public WishStatus convert(String status) {
		return WishStatus.of(status);
	}
}
