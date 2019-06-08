class Resposta:
    @staticmethod
    def error(mensagem: str) -> tuple:
        return {'status': 500, 'mensagem':mensagem}, 500

    @staticmethod
    def sucesso(mensagem: str) -> tuple:
        return {'status': 200, 'mensagem':mensagem}, 200

    @staticmethod
    def nao_aceito(mensagem: str) -> tuple:
        return {'status': 406, 'mensagem':mensagem}, 406

    @staticmethod
    def retorno(resposta: list) -> tuple:
        return {'resultado': resposta}, 200