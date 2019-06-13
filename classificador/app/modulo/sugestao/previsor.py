# import sys
# sys.path[0] = '/home/monopoly/Documents/pi2'
from collections import defaultdict

from loguru import logger

from app.servico.database import Database
from app.servico.utils import descompactar_modelo
from app.servico.firebase import buscar_eventos, cadastrar
from app.modulo.sugestao.grafo import Grafo, RelacoesPessoas


LISTA_MODELOS = list()
INDEX_INTERESSES = dict() 
VALORES = [list()]


def carregar_ram():
    global LISTA_MODELOS, INDEX_INTERESSES, VALORES

    modelos = Database().get_document('versao_modelo')[-1]

    logger.info(f"Versão modelo: {modelos['_id']}")

    for modelo in modelos['ids_modelos']:
        modelo = Database().get_document('modelo', {'_id': modelo}, {'modelo': 1, '0': 1, '1': 1, '_id': 0})[0]
        LISTA_MODELOS.append({'modelo': descompactar_modelo(modelo['modelo']), 0: modelo['0'], 1: modelo['1']})

    ordem_interesses = Database().get_document('ordem_interesse', {'_id': modelos['_id']})[-1]['interesse']

    for index, interesse in enumerate(ordem_interesses):
        INDEX_INTERESSES[interesse] = index
        VALORES[0].append(0)


def previsor(interesses):
    global LISTA_MODELOS, INDEX_INTERESSES, VALORES

    db = Database()

    valores = VALORES.copy()
    for interesse in interesses:
        if INDEX_INTERESSES.get(interesse, None):
            valores[0][INDEX_INTERESSES[interesse]] = 1
    
    grupos = list()
    for modelo in LISTA_MODELOS:
        previsao = modelo['modelo'].predict(valores)[0]
        x = modelo['modelo'].predict_proba(valores)
        print(x, x[0][previsao], modelo[previsao])


        if modelo[previsao] != 'outros' and x[0][previsao]:
            grupos.append(modelo[previsao])

    return grupos


def sugestao_eventos():
    logger.info("Executando Grafo")

    grafo_eventos = Grafo()
    eventos = buscar_eventos()

    # busca os eventos passados
    for evento in eventos[0]:
        id_evento = evento['id']
        for id_participante in evento['participantes']:
            grafo_eventos.inserir(id_participante, id_evento)

    # monta as ligações com os usuários
    grafo_networking = RelacoesPessoas(grafo_eventos.grafo)
    for es in eventos.values():
        for evento in es:
            for index, id1 in enumerate(evento['participantes']):
                for id2 in evento['participantes'][index+1:]:
                    grafo_networking.inserir(id1, id2)
                    grafo_networking.inserir(id2, id1)

    # armazena as recomendações
    recomendacao = defaultdict(list)
    relacoes = grafo_networking.buscar_relacoes()
    for evento in eventos[1]:
        id_evento = evento['id']
        for id_participante in evento['participantes']:
            for i in relacoes[id_participante]:
                recomendacao[i].append(id_evento)

    # salva no firebase
    for id_usuario in recomendacao:
        cadastrar(id_usuario, 'recomendacao', recomendacao[id_usuario])

    logger.info("FIM Grafo")


carregar_ram()


if __name__ == '__main__':
    from datetime import datetime
    ini = datetime.now()
    for _ in range(100):
        previsor(['apple/macintosh technology'])
    print(datetime.now() - ini)
