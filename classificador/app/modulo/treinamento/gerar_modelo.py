import numpy as np
from tqdm import tqdm
from loguru import logger

from app.servico.database import Database
from app.servico.utils import ler_csv, salvar_modelo
from app.modulo.treinamento.random_forest import RandomForest
from app.modulo.treinamento.pre_processamento import info_membros


def grupos() -> None:
    df_grupos = ler_csv(f'groups.csv', usar_colunas=['category.shortname', 'rating', 'group_id'])

    db = Database()
    for id_grupo, categoria, nota in tqdm(df_grupos.values):
        db.set_document('grupos', {'_id': id_grupo, 'categoria': categoria, 'nota': nota})


def gerar_dataset(qtd):
    df_interesses = ler_csv('members_topics.csv')
    it_df_membros = ler_csv('members.csv', usar_colunas=['member_id', 'group_id'], qtd_it=10000)

    df = info_membros(it_df_membros, df_interesses, qtd)
    logger.debug(f'Quantidade de registros: {len(df)}')
    df.fillna(0, inplace=True)
    df.drop_duplicates(inplace=True)
    df.drop(['id_membro'], axis=1, inplace=True)
    logger.debug(f'Quantidade de registros únicos: {len(df)}')

    colunas = list(df.columns)
    colunas.remove('categoria_grupo')
    colunas.insert(0, 'categoria_grupo')
    
    id_treinamento = Database().set_document('ordem_interesse', {'interesse': colunas[1:]}, auto=True)
    return df[colunas], id_treinamento

def montar_treinamento(qtd):
    treinamentos = list()

    df, id_treinamento = gerar_dataset(qtd)
    for grupo in df['categoria_grupo'].unique():
        massa = {
            'grupo': grupo,
            'dataset': df.loc[df['categoria_grupo'] == grupo].values,
            'previsores_treino': [],
            'classe_treino': [],
            'previsores_teste': [],
            'classe_teste': [],
            'modelo': None,
            'metricas': None,
            'qc1': 0,
            'qc2': 0
        }
        massa['qc1'] = len(massa['dataset'])
        for outros in df['categoria_grupo'].unique():
            if outros != grupo:
                falta = massa['qc1'] - massa['qc2']
                outros = df.loc[df['categoria_grupo'] == outros]
                outros['categoria_grupo'] = 'outros'
                valores = outros.values[:falta]
                
                if massa['qc2'] == massa['qc1']:
                    break
                    
                massa['qc2'] += len(valores)
                massa['dataset'] = np.concatenate((massa['dataset'], valores), axis=0)
        massa['classe_treino'] = massa['dataset'][:,0]
        massa['previsores_treino'] = massa['dataset'][:,1:]
        
                
        treinamentos.append(massa)

    return treinamentos, id_treinamento


def gerar_modelo(qtd):
    treinamentos, id_treinamento = montar_treinamento(qtd)

    ids_modelos = list()
    for dataset in treinamentos:
        logger.debug(f'Treinando {dataset["grupo"]}')
        modelo = RandomForest(200).treinar(dataset)
        ids_modelos.append(salvar_modelo(modelo))

    Database().set_document('versao_modelo', {'_id': id_treinamento, 'ids_modelos': ids_modelos})

    return id_treinamento
