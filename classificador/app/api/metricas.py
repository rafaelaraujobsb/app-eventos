from datetime import datetime

from loguru import logger
from flask import request
from flasgger import swag_from
from flask_restful import Resource

from app.servico.database import Database
from app.modulo.resposta_api import Resposta

class Metricas(Resource):
    @swag_from('../../doc/api/metricas_get.yml')
    def get(self):
        db = Database()

        modelos = db.get_document('versao_modelo')[-1]

        r = list()
        for id_modelo in modelos['ids_modelos']:
            resultado = db.get_document('modelo', {'_id': id_modelo}, {'_id': 0, 'metricas': 1, 'grupo': 1})[0]
            resultado['metricas'] = resultado['metricas']['macro avg']
            r.append(resultado)

        return Resposta.retorno(r)
