import re
from collections import defaultdict

from tqdm import tqdm
from pandas import DataFrame

from app.servico.database import Database
from app.servico.utils import ler_csv


INTERESSES = {
    'art', 'social', 'outdoors', 'dancing', 'technology', 'social networking',
    'photography', 'lgbt', 'sports and recreation', 'language & culture', 'board games', 'music'
}

CATEGORIAS = {
    'tech', 'socializing', 'language', 'outdoors-adventure', 'career-business', 'sports-recreation',
    'photography', 'parents-family', 'games', 'music', 'dancing', 'lgbt', 'lifestyle', 'arts-culture'
}


def interesse_membro(id: str, df_interesses: DataFrame) -> list:
    """
    Agrupa os intereses por id.

    :param str id: id do usuário
    """
    r = df_interesses['topic_key'].loc[df_interesses['member_id'] == id]
    
    if not r.empty:
        return r.tolist()

    return []   


# TODO: Validar a possibilidade de já ir inserindo em um dataframe
def info_membros(df_membros: DataFrame, df_interesses: DataFrame, qtd_membros: int) -> tuple:
    """
    Informação dos membros.

    :param DataFrame df_membros: Dataframe de membros
    :param int qtd_membros: quantida de membros a ser buscado

    :return: membros e interesses unicos
    :rtype: tuple
    """
    global INTERESSES, CATEGORIAS
    
    COLUNAS = ['member_id', 'group_id']
    membros = list()
    controlador = defaultdict(int)

    db = Database()
    for linhas in df_membros:
        for linha in tqdm(linhas.values):
            if sum(controlador.values()) >= qtd_membros*33:
                break
            else:
                id_membro, id_grupo = linha
                info_grupo = db.get_document('grupos', {'_id': int(id_grupo)}, {'nota': 0})[0]

                if controlador[info_grupo['categoria']] == qtd_membros or info_grupo['categoria'] not in CATEGORIAS:
                    continue

                mbr_interesses = interesse_membro(id_membro, df_interesses)
                interesses_filtrado = set()
                if any(mbr_interesses):
                    for interesse in mbr_interesses:
                        for e in interesse.split(','):
                            e = e.strip().lower()
                            if e and e in INTERESSES:
                                interesses_filtrado.add(e)

                    membro = {
                        'id_membro': id_membro,
                        'categoria_grupo': info_grupo['categoria']
                    }

                    for interesse in interesses_filtrado:
                        membro[interesse] = 1

                    membros.append(membro)
                    controlador[info_grupo['categoria']] += 1

        if sum(controlador.values()) >= qtd_membros*33:
            break

    return DataFrame(membros)


# def prepara_dataset(dataset: DataFrame) -> None:
#     """
#     Adiciona as colunas de interesses.

#     :param DataFrame dataset: Dataframe de treinamento
#     """
#     for i in range(len(dataset)):
#         col_interesse = dataset.iloc[i]['interesse']

#         for interesse in col_interesse:
#             for e in SEPARADOR.split(interesse)
#                 e = e.strip().lower()
#                 if e:
#                     dataset.at[i, e] = 1

#     dataset.fillna(0)
