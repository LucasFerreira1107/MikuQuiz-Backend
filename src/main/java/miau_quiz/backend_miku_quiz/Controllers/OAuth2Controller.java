package miau_quiz.backend_miku_quiz.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

@RestController
public class OAuth2Controller {

    @GetMapping(value = "/oauth2/success", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> oauth2Success(@RequestParam("token") String token) {
        String htmlContent = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <title>Login Successful</title>
                <meta charset="UTF-8">
                <style>
                    body { 
                        font-family: Arial, sans-serif; 
                        text-align: center; 
                        padding: 50px; 
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        color: white;
                    }
                    .container {
                        background: rgba(255, 255, 255, 0.1);
                        padding: 30px;
                        border-radius: 15px;
                        backdrop-filter: blur(10px);
                    }
                    .success-icon {
                        font-size: 48px;
                        margin-bottom: 20px;
                    }
                    .token {
                        background: rgba(255, 255, 255, 0.2);
                        padding: 10px;
                        border-radius: 5px;
                        word-break: break-all;
                        margin: 20px 0;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="success-icon">🎉</div>
                    <h1>Login Successful!</h1>
                    <p>You can now close this window and return to the app.</p>
                    <div class="token">Token: %s</div>
                    <script>
                        // Tenta enviar o token para o app
                        try {
                            if (window.ReactNativeWebView) {
                                window.ReactNativeWebView.postMessage(JSON.stringify({
                                    type: 'oauth2_success',
                                    token: '%s'
                                }));
                            }
                        } catch (e) {
                            console.log('Erro ao enviar mensagem para ReactNativeWebView:', e);
                        }
                        
                        // Tenta fechar a janela imediatamente
                        try {
                            window.close();
                        } catch (e) {
                            console.log('Não foi possível fechar a janela automaticamente');
                        }
                        
                        // Fallback: tenta fechar após 1 segundo
                        setTimeout(() => {
                            try {
                                window.close();
                            } catch (e) {
                                console.log('Tentativa de fechar janela falhou');
                            }
                        }, 1000);
                    </script>
                </div>
            </body>
            </html>
            """, token, token);
        
        return ResponseEntity.ok()
                .header("ngrok-skip-browser-warning", "69420")
                .body(htmlContent);
    }
}
