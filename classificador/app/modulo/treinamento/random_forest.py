from sklearn.pipeline import Pipeline
from sklearn.decomposition import PCA
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import GridSearchCV
from sklearn.ensemble import RandomForestClassifier


class RandomForest:
    def __init__(self, n_iter: int):
        self.random_grid = {
            'random_forest__n_estimators': [2, 12, 26, 28, 33, 46, 62, 78],
            'random_forest__max_features': ['log2', 'sqrt', 'auto'],
            'random_forest__max_depth': [2, 8, 12, 24, 33, 46, 60],
            'pca__n_components': [2, 3, 4]
        }
        self.pipe = Pipeline([('pca', PCA()), ('random_forest', RandomForestClassifier())])
        self.grid_cl = GridSearchCV(
            estimator=self.pipe, 
            param_grid=self.random_grid, 
            scoring='f1', verbose=1, n_jobs=-1, return_train_score=True
        )
        self.label_encoder_classe = LabelEncoder()

    def treinar(self, dataset):
        dataset = self.__trasnformar_dados(dataset)
        
        self.grid_cl.fit(dataset['previsores_treino'], dataset['classe_treino'])
        resultados = self.grid_cl.cv_results_
        #print(resultados)
        dataset['metricas'] = {'f1_teste': sum(resultados['mean_test_score'])/len(resultados['mean_test_score']), 'f1_treino': sum(resultados['mean_train_score'])/len(resultados['mean_train_score'])}
        dataset['modelo'] = self.grid_cl.best_estimator_

        return dataset

    def __trasnformar_dados(self, dataset):
        dataset['classe_treino'] = self.label_encoder_classe.fit_transform(dataset['classe_treino'])

        # dataset['previsores_treino'], dataset['previsores_teste'], dataset['classe_treino'], dataset['classe_teste'] = train_test_split(dataset['previsores_treino'], dataset['classe_treino'], test_size=.20, random_state=0)
        
        dataset['0'] = self.label_encoder_classe.inverse_transform([0])[0]
        dataset['1'] = self.label_encoder_classe.inverse_transform([1])[0]
        
        return dataset
