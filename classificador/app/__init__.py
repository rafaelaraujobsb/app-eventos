from loguru import logger
from flasgger import Swagger
from flask import Flask, jsonify

from app.api import api_bp
from app.modulo.resposta_api import Resposta


logger.add("app.log")

template = {
    "swagger": "2.0",
    "info": {
        "title": "Projeto Integrador 2",
        "description": """
            API para sugerir grupos aos participantes
        de acordo com seus interesses.
        """,
        "version": "0.0.5"
    }
}

app = Flask(__name__)
Swagger(app, template=template)

app.register_blueprint(api_bp, url_prefix='/api')

@app.errorhandler(404)
def page_not_found(e):
    msg, code = Resposta.error('URL inválida.')
    return jsonify(msg), code


@app.errorhandler(500)
def server_error(e):
    msg, code = Resposta.error('Erro interno.')
    return jsonify(msg), code
