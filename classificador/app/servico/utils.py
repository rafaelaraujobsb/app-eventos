import pickle

import pandas as pd

from app.config import LOCAL_DATASET
from app.servico.database import Database


def ler_csv(arquivo: str, usar_colunas: list = None, qtd_it: int = None, cod: str = 'iso-8859-1') -> pd.DataFrame:
    """
    Leitura de arquivos csv.

    :param str arquivo: nome do arquivo
    :param list usar_colunas: lista de colunas que será usada.
    :param int qtd_it: quantida de linhas por interação


    :return: DataFrame pandas
    :rtype: pd.Dataframe
    """
    return pd.read_csv(f'{LOCAL_DATASET}/{arquivo}', usecols=usar_colunas, chunksize=qtd_it, index_col=False, encoding='iso-8859-1')


def salvar_modelo(dataset: dict) -> str:
    db = Database()

    modelo_pk = pickle.dumps(dataset['modelo'])
    modelo = {
        'modelo': modelo_pk,
        'grupo': dataset['grupo'],
        'metricas': dataset['metricas'],
        '0': dataset['0'],
        '1': dataset['1'],
    }

    return db.set_document('modelo', modelo)

def descompactar_modelo(modelo_pickle):
    return pickle.loads(modelo_pickle)
