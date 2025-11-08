package entity;

public enum Llenado {
	VERDE(100), NARANJA(50), ROJO(0);

    // Campo para guardar el valor asociado
    private final int valor;

    // Constructor del enum (siempre es private)
    private Llenado(int valor) {
        this.valor = valor;
    }

    // Getter para acceder al valor
    public int getValor() {
        return valor;
    }
}
