package codesquard.app.domain.item;

import static codesquard.app.api.category.CategoryFixedFactory.*;
import static codesquard.app.api.oauth.OauthFixedFactory.*;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.item.ImageFixedFactory;
import codesquard.app.api.item.ItemFixedFactory;
import codesquard.app.api.item.WishFixedFactory;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.image.Image;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.wish.Wish;

class ItemTest extends IntegrationTestSupport {

	@BeforeEach
	void cleanup() {
		chatLogRepository.deleteAllInBatch();
		chatRoomRepository.deleteAllInBatch();
		wishRepository.deleteAllInBatch();
		imageRepository.deleteAllInBatch();
		itemRepository.deleteAllInBatch();
		categoryRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
		memberTownRepository.deleteAllInBatch();
	}

	@DisplayName("상품에 회원을 설정한다")
	@Test
	public void setMember() {
		// given
		Category category = createdFixedCategory();
		categoryRepository.save(category);

		Member member = createFixedMemberWithMemberTown();
		memberRepository.save(member);

		Item item = ItemFixedFactory.createFixedItem(null, category, new ArrayList<>(), new ArrayList<>(), 0L);

		// when
		item.setMember(member);

		// then
		Item saveItem = itemRepository.save(item);
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(saveItem.getMember()).isEqualTo(member);
			softAssertions.assertThat(member.getItems()).contains(saveItem).hasSize(1);
			softAssertions.assertAll();
		});
	}

	@DisplayName("상품에 카테고리를 설정한다")
	@Test
	public void setCategory() {
		// given
		Category category = createdFixedCategory();
		categoryRepository.save(category);

		Member member = createFixedMemberWithMemberTown();
		memberRepository.save(member);

		Item item = ItemFixedFactory.createFixedItem(member, null, new ArrayList<>(), new ArrayList<>(), 0L);

		// when
		item.setCategory(category);

		// then
		Item saveItem = itemRepository.save(item);
		Assertions.assertThat(saveItem.getCategory()).isEqualTo(category);
	}

	@DisplayName("상품에 이미지를 추가한다")
	@Test
	public void addImage() {
		// given
		Category category = createdFixedCategory();
		categoryRepository.save(category);

		Member member = createFixedMemberWithMemberTown();
		memberRepository.save(member);

		Item item = ItemFixedFactory.createFixedItem(member, category, new ArrayList<>(), new ArrayList<>(), 0L);

		List<Image> images = ImageFixedFactory.createFixedImages();
		// when
		images.forEach(item::addImage);

		// then
		Item saveItem = itemRepository.save(item);
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(saveItem.getImages()).hasSize(2).containsAll(images);
			softAssertions.assertThat(images.stream().map(Image::getItem))
				.allMatch(imageItem -> imageItem.equals(saveItem));
			softAssertions.assertAll();
		});
	}

	@Transactional
	@DisplayName("상품에 관심 상품을 추가한다")
	@Test
	public void addInterest() {
		// given
		Category category = createdFixedCategory();
		Member member = createFixedMemberWithMemberTown();

		Item item = ItemFixedFactory.createFixedItem(member, category, new ArrayList<>(), new ArrayList<>(),
			0L);

		Wish wish = WishFixedFactory.createWish(member);

		// when
		item.addWish(wish);

		// then
		categoryRepository.save(category);
		memberRepository.save(member);
		Item saveItem = itemRepository.save(item);
		itemRepository.findById(saveItem.getId()).orElseThrow();

		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(saveItem.getWishes()).hasSize(1).contains(wish);
			softAssertions.assertThat(wish.getItem()).isEqualTo(saveItem);
			softAssertions.assertAll();
		});
	}

	@DisplayName("한 상품에 대한 모든 채팅방의 채팅 개수를 가져온다")
	@Test
	public void getTotalChatLogCount() {
		// given
		Category category = createdFixedCategory();
		categoryRepository.save(category);
		Member member = createFixedMemberWithMemberTown();
		memberRepository.save(member);

		Item item = ItemFixedFactory.createFixedItem(member, category, new ArrayList<>(), new ArrayList<>(),
			0L);

		Item saveItem = itemRepository.save(item);

		// when
		int sum = saveItem.getTotalChatLogCount();

		// then
		Assertions.assertThat(sum).isZero();
	}
}
