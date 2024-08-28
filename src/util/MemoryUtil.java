package util;

public class MemoryUtil {
    public static long measureMemoryInKB() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc(); // Solicita a coleta de lixo para liberar memória inutilizada
        long memory = runtime.totalMemory() - runtime.freeMemory();
        return memory / 1024; // Converte bytes para kilobytes
    }
}



