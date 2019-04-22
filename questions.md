# Questions sur le "Figures Editor"

	Nom :
	Prénom :
	Groupe :

## Objectif
L'objectif de ce petit questionnaire est de vous faire étudier le code du projet "Figures Editor" afin que vous puissiez mieux comprendre son fonctionnement lorsque le mini-projet sur cet éditeur vous sera présenté la semaine prochaine.

## Travail à effectuer

Répondez aux questions ci-dessous directement dans ce fichier et déposez vos réponses sur le dépôt "ilo-questions-editor" avant le 07/04/2019 minuit.

### Pattern hunt
Recherchez dans le code du projet les différents Design patterns utilisés et listez les ci-dessous.

> Vous pourrez avantageusement utiliser les fonctions de recherche d'Eclipse Menu Search -> File -> Containing text.

> Vous pouvez aussi rechercher les utilisations d'un symbole (un type ou une variable) dans tout le projet : Sélectionnez un symbole puis clic droit pour faire apparaître le menu contextuel -> References -> Project

#### Liste des patterns utilisés dans le Figure Editor
* __Iterator__ : Inutile de le rechercher, il est partout dès que l'on a une collection comme dans `Vector<Figure> figures` dans la classe `figures.Drawing`, ou bien dans `Vector<FigureFilter<T>> filters` de la classe `filters.FigureFilters` par exemple.

### Model-View-Controller

Dans notre éditeur de figures, si la classe `figures.Drawing` correspond au modèle de données (l'ensemble des figures à dessiner) et la classe `widgets.DrawingPanel` correspond à la vue de ce modèle, quels seront le ou les contrôleurs du paradigme MVC ?

#### Les contrôleurs du MVC

* ...
