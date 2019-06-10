import pyrebase

# Var. ambiente
from app.config import (
    API_KEY, AUTH_DOMAIN ,DATABASE_APP,
    PROJETO, STORAGE, MSG_SENDER, APP_ID
)


config = {
    "apiKey": API_KEY,
    "authDomain": AUTH_DOMAIN,
    "databaseURL": DATABASE_APP,
    "projectId": PROJETO,
    "storageBucket": STORAGE,
    "messagingSenderId": MSG_SENDER,
    "appId": APP_ID
}


def cadastrar(cod_usuario, chave, valor):
    firebase = pyrebase.initialize_app(config)
    db = firebase.database()
    usuarios = db.child("Usuarios").get().val()
    usuarios[cod_usuario][chave] = valor

    data = {
        f'Usuarios/{cod_usuario}': usuarios[cod_usuario]
    }

    db.update(data)


def buscar_eventos():
    firebase = pyrebase.initialize_app(config)
    db = firebase.database()
    eventos = {0: [], 1: []}

    for id, evento in db.child("Eventos").get().val().items():
        evento['id'] = id
        
        if evento['ativo'] == 0:
            eventos[0].append(evento)
        else:
            eventos[1].append(evento)

    return eventos


if __name__ == '__main__':
    buscar_eventos()
    # print(db.child("Usuarios").get().val())
    # print(db.child("Usuarios").shallow().get().val())
    # print()
    # print(db.child("Usuarios").order_by_child("email").get())
    # usuarios = db.child("Usuarios").order_by_child("cpf").equal_to("04894670181").limit_to_first(1).get() # .order_by_key().equal_to('-LgIw4wmscvFnPreUSFD')
    # print(usuarios.val())
