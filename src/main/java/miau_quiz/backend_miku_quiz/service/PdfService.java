package miau_quiz.backend_miku_quiz.service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

import lombok.RequiredArgsConstructor;
import miau_quiz.backend_miku_quiz.dto.PersonalInfoDTO;
import miau_quiz.backend_miku_quiz.dto.RankingEntryDTO;

@Service
@RequiredArgsConstructor
public class PdfService {

	private final TemplateEngine templateEngine;
	private final UserService userService;
	private final RankingService rankingService;
	
	public byte[] generatePdfReport(UUID userId) throws DocumentException{
		PersonalInfoDTO user = userService.getPersonalDashboard(userId);
		List<RankingEntryDTO> ranking = rankingService.getGlobalRanking();
		
		Context context = new Context();
		context.setVariable("user", user);
		context.setVariable("ranking", ranking);
		
		String processedHtml = templateEngine.process("pdf-report-template", context);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(processedHtml);
		renderer.layout();
		renderer.createPDF(outputStream, false);
		renderer.finishPDF();
		
		return outputStream.toByteArray();
	}
	
	public byte[] generateRankingReport() throws DocumentException {
        // 1. Buscar o ranking e limitar ao Top 30
        List<RankingEntryDTO> top30Ranking = rankingService.getGlobalRanking().stream()
                .limit(30)
                .toList();

        // 2. Criar o contexto
        Context context = new Context();
        context.setVariable("ranking", top30Ranking);

        // 3. Processar e gerar o PDF
        String processedHtml = templateEngine.process("ranking-report", context);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(processedHtml);
        renderer.layout();
        renderer.createPDF(outputStream, false);
        renderer.finishPDF();

        return outputStream.toByteArray();
    }
	
	public byte[] generateUserStatsReport(UUID userId) throws DocumentException {
        // 1. Buscar os dados do utilizador
        PersonalInfoDTO userDto = userService.getPersonalDashboard(userId);

        // 2. Criar o contexto do Thymeleaf e adicionar as variáveis
        Context context = new Context();
        context.setVariable("user", userDto);

        // 3. Processar o template HTML com os dados
        String processedHtml = templateEngine.process("user-stats-report", context);

        // 4. Gerar o PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(processedHtml);
        renderer.layout();
        renderer.createPDF(outputStream, false);
        renderer.finishPDF();

        return outputStream.toByteArray();
    }
	
}
