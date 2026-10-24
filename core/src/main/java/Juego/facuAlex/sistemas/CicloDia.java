package Juego.facuAlex.sistemas;

public class CicloDia {

    // 24 h * 25 s = 10 minutos reales por día completo (ajustable)
    public static final float SEGUNDOS_POR_HORA = 12f;

    private int dia;
    private int hora;
    private float acumulador;

    public CicloDia() {
        dia = 1;
        hora = 17;          // arranca con el día ya establecido
        acumulador = 0f;
    }

    public void actualizar(float delta) {

        acumulador += delta;

        while (acumulador >= SEGUNDOS_POR_HORA) {

            acumulador -= SEGUNDOS_POR_HORA;
            hora++;

            if (hora >= 24) {
                hora = 0;
                dia++;
            }
        }
    }

    // 0 = pleno día, 1 = noche cerrada
    public float getOscuridad() {

    float h = hora + acumulador / SEGUNDOS_POR_HORA;

    if (h >= 18f && h < 22f) {
        return suavizar((h - 18f) / 4f);          // atardecer: 4 horas
    }

    if (h >= 22f || h < 4f) {
        return 1f;                                // noche
    }

    if (h < 7f) {
        return 1f - suavizar((h - 4f) / 3f);      // amanecer: 3 horas
    }

    return 0f;
}

    private float suavizar(float t) {
        return t * t * (3f - 2f * t);
    }

    public int getDia() {
        return dia;
    }
}