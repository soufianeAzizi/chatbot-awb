package net.attijariwafa.chatbotawb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.attijariwafa.chatbotawb.dto.PromptInputDto;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LLMService {

	private final OllamaChatModel chatModel;

	private final ResourceLoader resourceLoader;

	private final List<Message> conversationHistory = new ArrayList<>();

	public String prompt(PromptInputDto dto) {
//		List<Message> messages = new ArrayList<>();
		if (StringUtils.hasText(dto.getSystemTemplateName())) {
			Resource resource = resourceLoader.getResource("classpath:/prompts/" + dto.getSystemTemplateName());
			SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(resource);
			if (!MapUtils.isEmpty(dto.getSystemTemplateArgs())) {
				conversationHistory.add(systemPromptTemplate.createMessage(dto.getSystemTemplateArgs()));
			} else {
				conversationHistory.add(systemPromptTemplate.createMessage());
			}
		}
		if (StringUtils.hasText(dto.getUserTemplateName())) {
			Resource resource = resourceLoader.getResource("classpath:/prompts/" + dto.getUserTemplateName());
			PromptTemplate userPromptTemplate = new PromptTemplate(resource);
			if (!MapUtils.isEmpty(dto.getUserTemplateArgs())) {


//				messages.add(userPromptTemplate.createMessage(dto.getUserTemplateArgs()));
				conversationHistory.add(userPromptTemplate.createMessage(dto.getUserTemplateArgs()));
			} else {
				conversationHistory.add(userPromptTemplate.createMessage());
			}
		} else {
			conversationHistory.add(new UserMessage(dto.getRawUserMessage()));
		}

		OllamaOptions options = new OllamaOptions();
		if (dto.getTemperature() != null) {
			options.setTemperature(dto.getTemperature());
		}
		if (dto.getTopK() != null) {
			options.setTopK(dto.getTopK());
		}
		if (dto.getTopP() != null) {
			options.setTopP(dto.getTopP());
		}

		Prompt prompt = new Prompt(conversationHistory, options);
		log.info("Final Prompt object: {}", prompt);

		String response = chatModel.call(new Prompt(conversationHistory, options)).getResult().getOutput().getContent();
		log.info("LLM response: {}", response);
		return response;
	}

	public void clearHistory() {
		conversationHistory.clear();
		log.info("Conversation history cleared.");
	}
}
