from queue import Queue
from collections import defaultdict


class Grafo:
    def __init__(self):
        self.grafo = defaultdict(set)
        
    def inserir(self, origem, destino):
        self.grafo[origem].add(destino)

class RelacoesPessoas(Grafo):
    def __init__(self, grafo):
        super().__init__()
        self.grafo_eventos = grafo
        
    def vincular(self, usuario1, usuario2):
        if len(self.grafo_eventos[usuario1] & self.grafo_eventos[usuario2]) == 3:
            return True
        else:
            return False
        
    def buscar_relacoes(self):
        relacao = defaultdict(list)
        fila, visitados = Queue(), set(), 
        fila.put((list(self.grafo.keys())[0], None))
        
        while not fila.empty():
            usuario, vizinho = fila.get()
            
            if {(usuario, vizinho), (vizinho, usuario)} & visitados:
                continue
                            
            if self.vincular(usuario, vizinho):
                relacao[usuario].append(vizinho)
                relacao[vizinho].append(usuario)
            
            visitados.add((usuario, vizinho))
            for vizinho in self.grafo[usuario]:
                fila.put((vizinho, usuario))

        return relacao
