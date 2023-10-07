package codesquard.app;

import java.util.ArrayList;
import java.util.List;

import codesquard.app.api.errors.errorcode.CategoryErrorCode;
import codesquard.app.api.errors.exception.NotFoundResourceException;
import codesquard.app.domain.category.Category;

public class CategoryTestSupport {
	private static final List<Category> categories = List.of(
		new Category("디지털기기", "https://i.ibb.co/cxS7Fhc/digital.png"),
		new Category("인기매물", "https://i.ibb.co/LSkHKbL/star.png"),
		new Category("부동산", "https://i.ibb.co/41ScRXr/real-estate.png"),
		new Category("중고차", "https://i.ibb.co/bLW8sd7/car.png"),
		new Category("디지털기기", "https://i.ibb.co/cxS7Fhc/digital.png"),
		new Category("생활가전", "https://i.ibb.co/F5z7vV9/domestic.png"),
		new Category("가구/인테리어", "https://i.ibb.co/cyYH5V8/furniture.png"),
		new Category("유아동", "https://i.ibb.co/VNKYZTK/baby.png"),
		new Category("유아도서", "https://i.ibb.co/LrwjRdf/baby-book.png"),
		new Category("스포츠/레저", "https://i.ibb.co/hXVgTyd/sports.png"),
		new Category("여성잡화", "https://i.ibb.co/yPwkyg3/woman-accessories.png"),
		new Category("여성의류", "https://i.ibb.co/4fvj6SC/woman-apparel.png"),
		new Category("남성패션/잡화", "https://i.ibb.co/wwfyjyB/man-apparel.png"),
		new Category("게임/취미", "https://i.ibb.co/cwJ74M4/game.png"),
		new Category("뷰티/미용", "https://i.ibb.co/cXrrK0m/beauty.png"),
		new Category("반려동물용품", "https://i.ibb.co/CbwHdNr/pet.png"),
		new Category("도서/음반", "https://i.ibb.co/7WjKkdt/book.png"),
		new Category("티켓,교환권", "https://i.ibb.co/kBhhs2p/ticket.png"),
		new Category("생활", "https://i.ibb.co/T0mnp8m/kitchen.png"),
		new Category("가공식품", "https://i.ibb.co/S0rSyxr/processed-foods.png"),
		new Category("식물", "https://i.ibb.co/rwZhRqh/plant.png"),
		new Category("기타 중고물품", "https://i.ibb.co/tCyMPf5/etc.png"),
		new Category("삽니다", "https://i.ibb.co/g7Gc1w0/buy.png")
	);

	public static List<Category> getCategories() {
		return new ArrayList<>(categories);
	}

	public static Category findByName(String name) {
		return categories.stream()
			.filter(category -> category.getName().equals(name))
			.findAny()
			.orElseThrow(() -> new NotFoundResourceException(CategoryErrorCode.NOT_FOUND_CATEGORY));
	}
}
