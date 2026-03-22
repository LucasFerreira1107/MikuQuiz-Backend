package miau_quiz.backend_miku_quiz.service;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import miau_quiz.backend_miku_quiz.dto.AIGenerateQuizRequestDTO;
import miau_quiz.backend_miku_quiz.dto.QuizCreateDTO;

@Service
@RequiredArgsConstructor
public class AIService {

	private final ChatClient chatClient;
	
	private final ObjectMapper objectMapper; // Injetar o ObjectMapper para conversão manual

    public QuizCreateDTO generateQuizFromPrompt(AIGenerateQuizRequestDTO request) {
        
        String promptString = """
            Gere um quiz em formato JSON sobre o tema "{topic}".
            O título do quiz deve ser "{title}".
            A dificuldade deve ser "{difficulty}".
            O quiz deve ter exatamente {numberOfQuestions} perguntas.

            O JSON de resposta DEVE seguir estritamente e APENAS a estrutura da classe Java QuizCreateDTO.
            A estrutura da classe é a seguinte:
            - `title`: String (use o título fornecido)
            - `description`: String (uma breve descrição criativa sobre o tema)
            - `difficulty`: String (use a dificuldade fornecida)
            - `tagsId`: um array de strings vazio []
            - `timePerQuestion`: "VINTE"
            - `status`: "DRAFT"
            - `allowOffline`: false
            - `questions`: uma lista de objetos, onde cada objeto tem:
                - `text`: String (o texto da pergunta)
                - `answers`: uma lista de 4 objetos, onde cada objeto tem:
                    - `text`: String (o texto da resposta)
                    - `correct`: boolean (true para a resposta correta, false para as outras)
                    - `explanation`: String (uma breve explicação do porquê a resposta está correta, apenas para a resposta com correct=true)

            Regras importantes:
            1. Para cada pergunta, garanta que apenas UMA resposta tenha o campo "correct" como true e varia a posicao da resposta verdadeira.
            2. A sua saída deve ser APENAS o objeto JSON, sem nenhum texto, explicação ou formatação markdown antes ou depois dele.
            """;

        PromptTemplate promptTemplate = new PromptTemplate(promptString);
        Prompt prompt = promptTemplate.create(Map.of(
            "topic", request.topic(),
            "title", request.title(),
            "difficulty", request.difficulty(),
            "numberOfQuestions", request.numberOfQuestions()
        ));

        System.out.println("--- PROMPT ENVIADO PARA A IA ---\n" + prompt.getContents());
        
        try {
            // 1. Obter a resposta como uma String primeiro
            String jsonResponse = chatClient.prompt(prompt)
                    .call()
                    .content(); // .content() retorna a resposta como String

            System.out.println("--- RESPOSTA JSON RECEBIDA DA IA ---\n" + jsonResponse);

            if (jsonResponse == null || jsonResponse.isBlank()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "A IA retornou uma resposta vazia.");
            }

            // 2. Tentar converter a String para o nosso DTO
            return objectMapper.readValue(jsonResponse, QuizCreateDTO.class);

        } catch (Exception e) {
            System.err.println("ERRO AO PROCESSAR A RESPOSTA DA IA: " + e.getMessage());
            e.printStackTrace();
            // Lança uma exceção clara para o controller
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao processar a resposta da IA. Verifique os logs do servidor.", e);
        }
    }
}
