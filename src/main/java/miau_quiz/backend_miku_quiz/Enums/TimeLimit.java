package miau_quiz.backend_miku_quiz.Enums;

public enum TimeLimit {
	 // Define as constantes do enum, passando o valor em segundos para o construtor
    DEZ(10),
    QUINZE(15),
    VINTE(20),
    TRINTA(30);

    // Campo para armazenar o valor em segundos
    private final int seconds;

    // Construtor do enum (é sempre privado)
    TimeLimit(int seconds) {
        this.seconds = seconds;
    }

    // Método público para acessar o valor em segundos
    public int getSeconds() {
        return seconds;
    }
}
