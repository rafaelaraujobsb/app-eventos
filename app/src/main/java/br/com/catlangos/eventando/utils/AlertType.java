package br.com.catlangos.eventando.utils;

public enum AlertType {
    INFO(android.R.drawable.ic_dialog_info, "Informação"),
    WARN(android.R.drawable.ic_dialog_alert, "Alerta"),
    SUCESS(android.R.drawable.checkbox_on_background, "Sucesso"),
    ERROR(android.R.drawable.ic_delete, "Erro"),
    FEEDBACK(android.R.drawable.ic_dialog_info, "Deixe seu Feedback :)"),
    AGRADECIMENTO(android.R.drawable.ic_dialog_info, "Obrigado pelo Feedback!");

    // número inteiro que guarda o valor do ícone que será mostrado no
    // dialog
    private int drawable;

    // Título do dialog
    private String title;

    AlertType(int drawable, String title) {
        this.drawable = drawable;
        this.title = title;
    }

    public int getDrawable() {
        return this.drawable;
    }

    public String getTitle() {
        return this.title;
    }
}
