package codesquard.app.api.converter;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;

import codesquard.app.domain.item.ItemStatus;

@Component
public class ItemRequestConverter implements Converter<String, ItemStatus> {
	@Override
	public ItemStatus convert(String value) {
		return ItemStatus.of(value);
	}

	@Override
	public JavaType getInputType(TypeFactory typeFactory) {
		return typeFactory.constructType(String.class);
	}

	@Override
	public JavaType getOutputType(TypeFactory typeFactory) {
		return typeFactory.constructType(ItemStatus.class);
	}
}
