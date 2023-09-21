package codesquard.app.api.chat.request;

import javax.validation.constraints.NotBlank;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatLogSendRequest {
	
	@NotBlank(message = "메시지는 필수 정보입니다.")
	private String message;
}
