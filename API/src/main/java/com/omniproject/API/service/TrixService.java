package com.omniproject.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.omniproject.api.dto.TrixRequestDTO;
import com.omniproject.api.dto.TrixResponseDTO;
import com.omniproject.api.model.Task;
import com.omniproject.api.model.TipoRecorrencia;
import com.omniproject.api.model.User;
import com.omniproject.api.model.Workspace;
import com.omniproject.api.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class TrixService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final WorkspaceService workspaceService;
    private final TaskRepository taskRepository;
    private final ActivityLogService activityLogService;
    private final ObjectMapper objectMapper;

    public TrixService(RestTemplate restTemplate, WorkspaceService workspaceService, TaskRepository taskRepository, ActivityLogService activityLogService) {
        this.restTemplate = restTemplate;
        this.workspaceService = workspaceService;
        this.taskRepository = taskRepository;
        this.activityLogService = activityLogService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Essencial para lidar com LocalDate no JSON
    }

    public TrixResponseDTO processarComandoTrix(TrixRequestDTO request, User usuarioLogado) {
        try {
            // 1. Engenharia de Prompt (System Instruction escondida)
            String prompt = "Você é Trix, uma IA especialista em produtividade. " +
                    "Leia o texto do usuário e extraia as seguintes informações rigorosamente no formato JSON: " +
                    "nomeProjeto, dataVencimento (formato AAAA-MM-DD) e uma lista de tarefas (array de strings chamado 'tarefas'). " +
                    "Se não houver data, use null. O texto do usuário é: [" + request.getTextoUsuario() + "]. " +
                    "Responda APENAS o JSON puro, sem textos extras, sem explicações e sem blocos de markdown (```json).";

            // 2. Preparar a chamada HTTP para o Google Gemini
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
            URI uri = URI.create(url);
            
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Passando a chave pelo Header (Melhor Prática e mais seguro)
            headers.set("x-goog-api-key", apiKey.trim());
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 3. Fazer a requisição POST
            ResponseEntity<String> response = restTemplate.postForEntity(uri, entity, String.class);

            // 4. Navegar na árvore do JSON de resposta do Gemini
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            String jsonOutput = rootNode.path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text").asText();

            // Limpeza de segurança caso a IA mande blocos de markdown apesar do prompt
            jsonOutput = jsonOutput.replaceAll("```json", "").replaceAll("```", "").trim();

            // 5. Converter o JSON gerado em nosso DTO tipado
            TrixResponseDTO responseDTO = objectMapper.readValue(jsonOutput, TrixResponseDTO.class);

            // 6. Orquestração: Criar Workspace e amarrar as Tarefas
            Workspace novoProjeto = new Workspace();
            novoProjeto.setNome(responseDTO.getNomeProjeto());
            if (responseDTO.getDataVencimento() != null) {
                novoProjeto.setDataEntrega(responseDTO.getDataVencimento());
            }
            
            Workspace projetoCriado = workspaceService.criarWorkspace(novoProjeto, usuarioLogado);

            if (responseDTO.getTarefas() != null && !responseDTO.getTarefas().isEmpty()) {
                for (String tituloTarefa : responseDTO.getTarefas()) {
                    Task task = new Task();
                    task.setTitulo(tituloTarefa);
                    task.setStatus("PENDENTE");
                    task.setWorkspace(projetoCriado);
                    task.setTipoRecorrencia(TipoRecorrencia.NENHUMA);
                    Task taskCriada = taskRepository.save(task);
                    
                    String descricao = activityLogService.formatarAcaoCriacaoTarefa(taskCriada.getTitulo(), usuarioLogado.getNome() + " (via Trix)");
                    activityLogService.registrarAcao(descricao, usuarioLogado, projetoCriado, taskCriada);
                }
            }

            return responseDTO;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar comando com a Trix IA: " + e.getMessage(), e);
        }
    }
}