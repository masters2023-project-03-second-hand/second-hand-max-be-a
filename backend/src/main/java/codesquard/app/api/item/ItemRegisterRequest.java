package codesquard.app.api.item;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRegisterRequest {

	@NotBlank(message = "제목을 입력해주세요.")
	private String title;
	private Long price;
	private String content;
	private String region;
	private String status;
	private Long categoryId;
	private String categoryName;

}
