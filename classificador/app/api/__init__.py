from flask import Blueprint
from flask_restful import Api

from app.api.modelo import Treinar, Previsor
from app.api.metricas import Metricas

api_bp = Blueprint('api', __name__)
api = Api(api_bp)

api.add_resource(Previsor, '/sugestao-grupo')
api.add_resource(Treinar, '/treinador')
api.add_resource(Metricas, '/metricas')

