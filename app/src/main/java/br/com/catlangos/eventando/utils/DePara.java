package br.com.catlangos.eventando.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DePara {
    private HashMap<String, String> categorias = new HashMap<>();
    private List<String> categoriasUsuario = new ArrayList<>();

    public DePara(List<String> categoriasUsuario) {
        montarDePara();
        this.categoriasUsuario = categoriasUsuario;
    }

    public List<String> getCategoriasTraduzidas() {
        List<String> categoriasTraduzidas = new ArrayList<>();
        for(Integer i = 0; i < this.categoriasUsuario.size(); i++) {
            categoriasTraduzidas.add(this.categorias.get(this.categoriasUsuario.get(i)));
        }

        return categoriasTraduzidas;
    }

    private void montarDePara() {
        this.categorias.put("arts-culture","Arte e Cultura");
        this.categorias.put("book-clubs","Clubes de Livros");
        this.categorias.put("career-business","Carreira Profissional");
        this.categorias.put("cars-motorcycles","Carros e Motocicletas");
        this.categorias.put("writing","Escrita");
        this.categorias.put("tech","Tecnologia");
        this.categorias.put("support" ,"Apoio e Suporte");
        this.categorias.put("sports-recreation", "Esportes e Recreação");
        this.categorias.put("socializing" ,"Socializar");
        this.categorias.put("singles" ,"Solteiros");
        this.categorias.put("shows","Shows");
        this.categorias.put("sci-fi-fantasy","Ficção Científica e Fantasia");
        this.categorias.put("religion-beliefs","Crenças Religiosas");
        this.categorias.put("photography","Fotografia");
        this.categorias.put("pets-animals","Animais de Estimação");
        this.categorias.put("parents-family","Pais e Familia");
        this.categorias.put("paranormal","Paranormal");
        this.categorias.put("outdoors-adventure","Aventura ao ar livre");
        this.categorias.put("new-age-spirituality","Nova era e Espiritualidade");
        this.categorias.put("music","Música");
        this.categorias.put("movies-film","Filmes");
        this.categorias.put("lifestyle","Estilo de Vida");
        this.categorias.put("lgbt","lgbt");
        this.categorias.put("language","Lingua");
        this.categorias.put("hobbies-crafts","Hobbies e Artesanatos");
        this.categorias.put("health-wellbeing","Saúde e Bem Estar");
        this.categorias.put("government-politics", "Governo e Política");
        this.categorias.put("games","Jogos");
        this.categorias.put("food-drink","Comida e Bebida");
        this.categorias.put("fitness","Ginástica");
        this.categorias.put("fashion-beauty","Moda e Beleza");
        this.categorias.put("education-learning","Educação e Aprendizagem");
        this.categorias.put("dancing","Dançando");
        this.categorias.put("community-environment","Comunidade e Ambiente");
    }
}
