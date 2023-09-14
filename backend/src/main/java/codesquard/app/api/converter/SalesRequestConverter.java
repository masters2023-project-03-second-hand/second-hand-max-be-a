package codesquard.app.api.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import codesquard.app.domain.sales.SalesStatus;

@Component
public class SalesRequestConverter implements Converter<String, SalesStatus> {
	@Override
	public SalesStatus convert(String status) {
		return SalesStatus.of(status);
	}
}
