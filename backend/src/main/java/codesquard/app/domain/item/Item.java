package codesquard.app.domain.item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import codesquard.app.domain.category.Category;
import codesquard.app.domain.chat.ChatRoom;
import codesquard.app.domain.image.Image;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.wish.Wish;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	private String content;
	private Long price;
	@Enumerated(value = EnumType.STRING)
	private ItemStatus status;
	private String region;
	private LocalDateTime createdAt;
	private String thumbnailUrl;
	private LocalDateTime modifiedAt;
	private Long wishCount = 0L;
	private Long chatCount = 0L;
	private Long viewCount = 0L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@OneToMany(mappedBy = "item", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private List<Wish> wishes = new ArrayList<>();

	@OneToMany(mappedBy = "item", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private List<ChatRoom> chatRooms = new ArrayList<>();

	@OneToMany(mappedBy = "item", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private List<Image> images = new ArrayList<>();

	public Item(Long id) {
		this.id = id;
	}

	@Builder
	public Item(String title, String content, Long price, ItemStatus status, String region, LocalDateTime createdAt,
		String thumbnailUrl, LocalDateTime modifiedAt, Long wishCount, Long chatCount, Long viewCount) {
		this.title = title;
		this.content = content;
		this.price = price;
		this.status = status;
		this.region = region;
		this.createdAt = createdAt;
		this.thumbnailUrl = thumbnailUrl;
		this.modifiedAt = modifiedAt;
		this.wishCount = wishCount;
		this.chatCount = chatCount;
		this.viewCount = viewCount;
	}

	public static Item create(String title, String content, Long price, ItemStatus status, String region,
		LocalDateTime createdAt, Long viewCount) {
		return Item.builder()
			.title(title)
			.content(content)
			.price(price)
			.status(status)
			.region(region)
			.createdAt(createdAt)
			.viewCount(viewCount)
			.build();
	}

	public void changeMember(Member member) {
		this.member = member;
		addItemBy(member);
	}

	private void addItemBy(Member member) {
		if (member == null) {
			return;
		}

		if (!member.containsItem(this)) {
			member.addItem(this);
		}
	}

	public void changeCategory(Category category) {
		this.category = category;
	}

	public void addImage(Image image) {
		if (image == null) {
			return;
		}
		if (!containsImage(image)) {
			this.images.add(image);
		}
		image.changeItem(this);
	}

	public void addWish(Wish wish) {
		if (wish == null) {
			return;
		}
		if (!containsWish(wish)) {
			this.wishes.add(wish);
		}
		wish.changeItem(this);
	}

	public void addChatRoom(ChatRoom chatRoom) {
		if (chatRoom == null) {
			return;
		}
		if (!containsChatRoom(chatRoom)) {
			chatRooms.add(chatRoom);
		}
		chatRoom.changeItem(this);
	}

	public int countTotalChatLog() {
		return chatRooms.stream()
			.mapToInt(ChatRoom::sizeChatLogs)
			.sum();
	}

	public boolean containsChatRoom(ChatRoom chatRoom) {
		return chatRooms.contains(chatRoom);
	}

	public boolean containsImage(Image image) {
		return images.contains(image);
	}

	public boolean containsWish(Wish wish) {
		return wishes.contains(wish);
	}

	public void wishRegister() {
		this.wishCount++;
	}

	public void wishCancel() {
		this.wishCount--;
	}

	public List<String> getImageUrls() {
		return images.stream()
			.map(Image::getImageUrl)
			.collect(Collectors.toUnmodifiableList());
	}

	@Override
	public String toString() {
		return String.format("%s, %s(id=%d, title=%s, price=%d, status=%s, region=%s, viewCount=%d)",
			"상품", this.getClass().getSimpleName(), id, title, price, status, region, viewCount);
	}
}
