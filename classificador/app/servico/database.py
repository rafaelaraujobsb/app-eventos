from pymongo import MongoClient

from app.config import DATABASE, HOST_DB


class ModDatabase():
    """ 
    Módulo mongo
    """
    def __init__(self, db, user=None, password=None, host='localhost', port=27017): 
        """ 
        Faz a conexão com o db.
        """
        if user:
            self.__client = MongoClient(f'mongodb://{user}:{password}@{host}:{port}/{db}')
        else:
            self.__client = MongoClient(f'mongodb://{host}:{port}/{db}')

        self.__db = self.__client.get_database()   

    def get_names_collections(self):
        """ 
        Busca todas as collections.
        """
        return self.__db.collection_names()

    def get_document(self, collection, filter=None, visible=None, max=0, sort=[('_id', 1)]):
        """ 
        Busca todos os documentos encontrados.

        :param str collection: nome da collection
        :param dict filter: filtro da busca
        :param dict visible: colunas que serão mostradas ou não
        :param int max: quantidade máxima de matches 
        :param list sort: | ordenação do resultado (ASCENDING = 1, DESCENDING = -1)
                          | Exemplo: [('_id', 1)]

        :returns: resultado da busca
        :rtype: list
        """ 

        return [ r for r in self.__db[collection].find(filter, visible).sort(sort).limit(max) ]

    def get_distinct(self, collection, column):
        """ 
        Busca todos os dados que são único.

        :param str collection: nome da collection
        :param str column: coluna a ser verificada

        :returns: exemplos unicos e quantas vezes eles se repetem
        :rtype: tuple
        """
        unq = self.__db[collection].distinct(column)
        count = [ self.__db[collection].find({column: r}).count() for r in unq ]

        return unq, count

    def __next_id(self, collection):
        """ 
        Faz a geração de id para substir o objectid.

        :param str collection: nome da collection

        :returns: id sequencial gerado
        :rtype: int
        """
        try:
            _id = self.__db.seqs.find_and_modify({'_id': collection}, {'$inc': {'id': 1}}, upsert=True)['id']
        except TypeError:
            _id = self.__db.seqs.find_and_modify({'_id': collection}, {'$inc': {'id': 1}}, upsert=True)['id']

        return _id

    def set_document(self, collection, value, auto=False):
        """ 
        Insere um documento.

        :param str collection: nome da collection
        :param dict value: valor que será inserido
        :param bool auto: ativa o uso de ids

        :returns: id sequencial gerado
        :rtype: int

        .. note::
            Ao usar `auto` como True será usadao um int sequencial como _id.
        """
        if auto:
            value['_id'] = self.__next_id(collection)

        return self.__db[collection].insert_one(value).inserted_id

    def update_document(self, collection, filter, value):
        """ 
        Altera um ou mais documentos.

        :param str collection: nome da collection
        :param dict filter: filtro da busca
        :param dict value: alteração a ser realizada

        :returns: quantidade de arquivos alterado
        :rtype: int

        .. note::
            `value` não precisa conter `$set`
        """

        if '$push' not in value:
            value = {'$set': value}

        result = self.__db[collection].update_many(filter, value)

        return result.modified_count

    def len_collection(self, collection, filter=None):
        return self.__db[collection].find(filter).count()

    def drop_collection(self, collection):
        """ 
        Deleta uma collection

        :param str collection: nome da collection
        """
        self.__db[collection].drop()
        self.__db.seqs.delete_one({'_id': collection})

    def __del__(self):
        """ 
        Encerra a conexão quando o objeto é destruído.
        """
        self.__client.close()

        self.__db.seqs.delete_one({'_id': collection})

    def __del__(self):
        self.__client.close()


class Database(ModDatabase):
    def __init__(self):
        super().__init__(DATABASE, host=HOST_DB)
