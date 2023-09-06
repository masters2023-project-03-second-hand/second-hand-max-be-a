package codesquard.app.domain.item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import codesquard.app.api.item.ItemRegisterRequest;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.chat.ChatRoom;
import codesquard.app.domain.image.Image;
import codesquard.app.domain.interest.Interest;
import codesquard.app.domain.member.Member;
import lombok.AllArgsConstructor;
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
	private ItemStatus status;
	private String region;
	private LocalDateTime createdAt;
	private Long viewCount;
	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
	private List<Image> images = new ArrayList<>();
	private LocalDateTime modifiedAt;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;
	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
	private List<Interest> interests = new ArrayList<>();

	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
	private List<ChatRoom> chatRooms = new ArrayList<>();

	public Item(String title, String content, Long price, ItemStatus status,
		String region,
		Member member,
		LocalDateTime createdAt,
		Long viewCount) {
		this.title = title;
		this.content = content;
		this.price = price;
		this.status = status;
		this.region = region;
		this.member = member;
		this.createdAt = createdAt;
		this.viewCount = viewCount;
	}

	//== 연관관계 메소드 ==//
	public void setMember(Member member) {
		this.member = member;
		if (this.member != null && !this.member.getItems().contains(this)) {
			this.member.addItem(this);
		}
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public void addImage(Image image) {
		if (image != null && !this.images.contains(image)) {
			this.images.add(image);
		}
		if (image != null) {
			image.setItem(this);
		}
	}

	public void addInterest(Interest interest) {
		if (interest != null && !this.interests.contains(interest)) {
			this.interests.add(interest);
		}
		if (interest != null) {
			interest.setItem(this);
		}
	}

	public void addChatRoom(ChatRoom chatRoom) {
		if (chatRoom != null && !chatRooms.contains(chatRoom)) {
			chatRooms.add(chatRoom);
		}
		if (chatRoom != null) {
			chatRoom.setItem(this);
		}
	}

	public int getTotalChatLogCount() {
		return chatRooms.stream()
			.mapToInt(ChatRoom::getChatLogsSize)
			.sum();
	}
	//== 연관관계 메소드 종료 ==//

	public static Item toEntity(ItemRegisterRequest request, Member member) {
		return new Item(
			request.getTitle(),
			request.getContent(),
			request.getPrice(),
			ItemStatus.of(request.getStatus()),
			request.getRegion(),
			member,
			LocalDateTime.now(),
			0L);
	}

	public static Item create(String title, String content, Long price, ItemStatus status, String region,
		LocalDateTime createdAt, Member member, Category category, List<Image> images, List<Interest> interests,
		Long viewCount) {
		Item item = new Item(title, content, price, status, region, member, createdAt, viewCount);
		item.setMember(member);
		item.setCategory(category);
		images.forEach(item::addImage);
		interests.forEach(item::addInterest);
		return item;
	}

	@Override
	public String toString() {
		return String.format("%s, %s(id=%d, title=%s, price=%d, status=%s, region=%s, viewCount=%d)",
			"상품", this.getClass().getSimpleName(), id, title, price, status, region, viewCount);
	}

}
