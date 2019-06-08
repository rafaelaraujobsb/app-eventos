from datetime import datetime

from loguru import logger
from flask import request
from flasgger import swag_from
from flask_restful import Resource

from app.modulo.resposta_api import Resposta
from app.modulo.treinamento.gerar_modelo import gerar_modelo
from app.modulo.sugestao.previsor import previsor


class Previsor(Resource):
    @swag_from('../../doc/api/previsorgrupo_post.yml')
    def post(self):
        json = request.json

        if not json:
            logger.debug(request.headers)
            return Resposta.nao_aceito('Envie um JSON!')
        elif type(json.get('cod_usuario', None)) == str and type(json.get('interesses', None)) == list:
            logger.debug(f'ID: {json["cod_usuario"]}')
            return Resposta.retorno(previsor(json['interesses']))
        else:
            return Resposta.nao_aceito('JSON inválido!')


class Treinar(Resource):
    @swag_from('../../doc/api/treinamento_post.yml')
    def post(self):
        json = request.json
        
        if json.get('senha', None) == 'iesbnota5noMEC' and json.get('quantidade', None) and json['quantidade'] >= 100:
            ini = datetime.now()
            id = gerar_modelo(json['quantidade'])
            logger.debug(f'Versão modelo: {id} - Tempo: {datetime.now() - ini}')
            return Resposta.sucesso('Treinamento realizado!')
        else:
            return Resposta.nao_aceito('JSON inválido!')
