# import sys
# sys.path[0] = '/home/monopoly/Documents/pi2'

from loguru import logger

from app.servico.database import Database
from app.servico.utils import descompactar_modelo


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


carregar_ram()


if __name__ == '__main__':
    from datetime import datetime
    ini = datetime.now()
    for _ in range(100):
        previsor(['apple/macintosh technology'])
    print(datetime.now() - ini)
