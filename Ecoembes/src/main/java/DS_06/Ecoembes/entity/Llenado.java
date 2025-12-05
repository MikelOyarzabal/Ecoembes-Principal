package DS_06.Ecoembes.entity;

public enum Llenado {
    VERDE(25),    // 25% ocupado
    AMARILLO(50), // 50% ocupado
    ROJO(75),     // 75% ocupado
    LLENO(100);   // 100% ocupado
    
    private final int valor;
    
    Llenado(int valor) {
        this.valor = valor;
    }
    
    public int getValor() {
        return valor;
    }
}