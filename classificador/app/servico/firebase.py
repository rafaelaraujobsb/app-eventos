import pyrebase

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


def cadastrar_categorias(cod_usuario, categorias):
    firebase = pyrebase.initialize_app(config)
    db = firebase.database()
    usuarios = db.child("Usuarios").get().val()
    usuarios[cod_usuario]['categorias'] = categorias

    data = {
        f'Usuarios/{cod_usuario}': usuarios[cod_usuario]
    }

    db.update(data)


if __name__ == '__main__':
    firebase = pyrebase.initialize_app(config)
    db = firebase.database()
    db.child("Usuarios").get()
    usuarios = db.child("Usuarios").get().val()

    usuarios['-LgIw4wmscvFnPreUSFD']['interesses'] = ['a', 'b', 'c']

    data = {
        'Usuarios/-LgIw4wmscvFnPreUSFD': usuarios['-LgIw4wmscvFnPreUSFD']
    }

    print(usuarios['-LgIw4wmscvFnPreUSFD'])
    db.update(data)
    # print(db.child("Usuarios").get().val())
    # print(db.child("Usuarios").shallow().get().val())
    # print()
    # print(db.child("Usuarios").order_by_child("email").get())
    # usuarios = db.child("Usuarios").order_by_child("cpf").equal_to("04894670181").limit_to_first(1).get() # .order_by_key().equal_to('-LgIw4wmscvFnPreUSFD')
    # print(usuarios.val())
