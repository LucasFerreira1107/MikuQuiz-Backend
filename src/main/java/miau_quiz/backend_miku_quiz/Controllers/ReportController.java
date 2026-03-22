package miau_quiz.backend_miku_quiz.Controllers;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

import lombok.RequiredArgsConstructor;
import miau_quiz.backend_miku_quiz.dto.RankingEntryDTO;
import miau_quiz.backend_miku_quiz.entity.User;
import miau_quiz.backend_miku_quiz.security.CustomAuthentication;
import miau_quiz.backend_miku_quiz.service.PdfService;
import miau_quiz.backend_miku_quiz.service.RankingService;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

	private final PdfService pdfService;
	private final RankingService rankingService;
	
	@GetMapping("/user-summary")
	public ResponseEntity<byte[]> downloadUserSummaryReport(Authentication authentication){
		try {
			User user = ((CustomAuthentication) authentication).getUser();
			byte[] pdfBytes = pdfService.generatePdfReport(user.getUserId());
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("attachment", "MikuQuiz_Report.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
            
		}catch(DocumentException e) {
			return ResponseEntity.internalServerError().build();
		}
	}
	
	@GetMapping("/user-stats")
    public ResponseEntity<byte[]> downloadUserStatsReport(Authentication authentication) {
        try {
            User currentUser = ((CustomAuthentication) authentication).getUser();
            byte[] pdfBytes = pdfService.generateUserStatsReport(currentUser.getUserId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "MikuQuiz_Stats.pdf");

            return ResponseEntity.ok().headers(headers).body(pdfBytes);
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/ranking")
    public ResponseEntity<byte[]> downloadRankingReport() {
        try {
            byte[] pdfBytes = pdfService.generateRankingReport();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "MikuQuiz_Ranking.pdf");
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    
   
}
