package net.attijariwafa.chatbotawb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromptInputDto {
	private String systemTemplateName;
	private Map<String, Object> systemTemplateArgs;
	private String userTemplateName;
	private Map<String, Object> userTemplateArgs;
	private String rawUserMessage;   // Le user message
	private Integer topK;
	private Double topP;
	private Double temperature;
}
