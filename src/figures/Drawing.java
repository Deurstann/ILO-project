package figures;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;
import java.util.SortedSet;
import java.util.Vector;
import java.util.stream.Stream;

import figures.enums.FigureType;
import figures.enums.LineType;
import filters.FigureFilter;
import filters.FigureFilters;
import history.Memento;
import history.Originator;
import utils.Signature;

/**
 * Classe contenant l'ensemble des figures à dessiner (LE MODELE)
 * @author davidroussel
 */
public class Drawing extends Observable implements Originator<Figure>, Signature
{
	/**
	 * Liste des figures à dessiner (protected pour que les classes du même
	 * package puissent y accéder)
	 */
	protected Vector<Figure> figures;

	/**
	 * Liste triée des indices (uniques) des figures sélectionnées de
	 * {@link #figures}.
	 * On peut savoir si une {@link Figure} est sélectionnée en l'interrogeant
	 * avec sa méthode {@link Figure#isSelected()}, mais on gardera les indices
	 * des figures sélectionnées dans {@link #selectionIndex} pour aller plus
	 * vite sur les opérations qui concernent uniquement les figures
	 * sélectionnées
	 */
	protected SortedSet<Integer> selectionIndex;

	/**
	 * Figure située sous le curseur.
	 * Déterminé par {@link #getFigureAt(Point2D)}
	 */
	private Figure selectedFigure;

	/**
	 * Le type de figure à créer (pour la prochaine figure)
	 */
	private FigureType type;

	/**
	 * La couleur de remplissage courante (pour la prochaine figure)
	 */
	private Paint fillPaint;

	/**
	 * La couleur de trait courante (pour la prochaine figure)
	 */
	private Paint edgePaint;

	/**
	 * La largeur de trait courante (pour la prochaine figure)
	 */
	private float edgeWidth;

	/**
	 * Le type de trait courant (sans trait, trait plein, trait pointillé,
	 * pour la prochaine figure)
	 */
	private LineType edgeType;

	/**
	 * Le type de trait en fonction de {@link #type} et
	 * {@link #edgeWidth}
	 */
	private BasicStroke stroke;

	/**
	 * Etat de filtrage des figures dans le flux de figures fournit par
	 * {@link #stream()}
	 * Lorsque {@link #filtering} est true le dessin des figures est filtré
	 * par l'ensemble des filtres présents dans {@link #shapeFilters},
	 * {@link #fillColorFilter}, {@link #edgeColorFilter} et
	 * {@link #lineFilters}.
	 * Lorsque {@link #filtering} est false, toutes les figures sont fournies
	 * dans le flux des figures.
	 * @see #stream()
	 */
	private boolean filtering;

	/**
	 * Filtres à appliquer au flux des figures pour sélectionner les types
	 * de figures à afficher
	 * @see #stream()
	 */
	private FigureFilters<FigureType> shapeFilters;

	/**
	 * Filtre à appliquer au flux des figures pour sélectionner les figures
	 * ayant une couleur particulière de remplissage
	 * @see FillColorFilter
	 */
	private FigureFilter<Paint> fillColorFilter;

	/**
	 * Filtre à appliquer au flux des figures pour sélectionner les figures
	 * ayant une couleur particulière de trait
	 * @see EdgeColorFilter
	 */
	private FigureFilter<Paint> edgeColorFilter;

	/**
	 * Filtres à applique au flux des figures pour sélectionner les figures
	 * ayant un type particulier de lignes
	 * @see LineFilter
	 */
	private FigureFilters<LineType> lineFilters;

	/**
	 * Constructeur de modèle de dessin
	 */
	public Drawing()
	{
		/*
		 * TODO Initialisation de l'ensemble des attributs
		 * 	- collections
		 * 		- figures
		 * 		- selectionIndex avec un TreeSet dont le comparateur sera Integer::compare
		 * 	- filtres
		 * 		- shapeFilters
		 * 		- fillColorFilter
		 * 		- edgeColorFilter
		 * 		- lineFilters
		 * 	- Paints et edge
		 * 		- fillPaint
		 * 		- edgePaint
		 * 		- edgeWidth : 1
		 * 		- edgeType : Voir LineType
		 * 		- stroke : avec edgeWidth & edgeType en utilisant la StrokeFactory
		 * 	- autres
		 * 		- filtering : non
		 * 		- selectedFigure : aucune
		 */
		figures = new Vector<Figure>();

		System.out.println(getClassName() + "::" + getMethodName()
		    + " Drawing not properly initialized yet");
	}

	/**
	 * Nettoyage avant destruction
	 */
	@Override
	protected void finalize()
	{
		/*
		 * TODO effacement des collections et mise à null des attributs
		 * pour faciliter le travil du GC
		 */
	}

	/**
	 * Mise à jour du ou des {@link Observer} qui observent ce modèle. On place
	 * le modèle dans un état "changé" puis on notifie les observateurs.
	 */
	public void update()
	{
		setChanged();
		notifyObservers(); // pour que les observateurs soient mis à jour
	}

	/**
	 * Accesseur du type de figure à générer
	 * @return le type de figure sélectionné
	 */
	public FigureType getFigureType()
	{
		return type;
	}

	/**
	 * Mise en place d'un nouveau type de figure à générer
	 * @param type le nouveau type de figure
	 */
	public void setFigureType(FigureType type)
	{
		this.type = type;
	}

	/**
	 * Accesseur de la couleur de remplissage courante des figures
	 * @return la couleur de remplissage courante des figures
	 */
	public Paint getFillpaint()
	{
		return fillPaint;
	}

	/**
	 * Mise en place d'une nouvelle couleur de remplissage
	 * @param fillPaint la nouvelle couleur de remplissage
	 */
	public void setFillPaint(Paint fillPaint)
	{
		this.fillPaint = fillPaint;
	}

	/**
	 * Accesseur de la couleur de trait courante des figures
	 * @return la couleur de remplissage courante des figures
	 */
	public Paint getEdgePaint()
	{
		return edgePaint;
	}

	/**
	 * Mise en place d'une nouvelle couleur de trait
	 * @param edgePaint la nouvelle couleur de trait
	 */
	public void setEdgePaint(Paint edgePaint)
	{
		this.edgePaint = edgePaint;
	}

	/**
	 * Accesseur du trait courant des figures
	 * @return le trait courant des figures
	 */
	public BasicStroke getStroke()
	{
		return stroke;
	}

	/**
	 * Accesseur de la largeur de ligne
	 * @return la largeur de ligne courante
	 */
	public float getEdgeWidth()
	{
		return edgeWidth;
	}

	/**
	 * Mise en place d'un nouvelle épaisseur de trait
	 * @param width la nouvelle épaisseur de trait
	 */
	public void setEdgeWidth(float width)
	{
		edgeWidth = width;
		/*
		 * Il faut regénérer le stroke
		 * TODO Remplacer la un stroke de la StrokeFactory
		 */
		stroke = null;
	}

	/**
	 * Accesseur du type de trait
	 * @return le type de trait
	 */
	public LineType getEdgeType()
	{
		return edgeType;
	}

	/**
	 * Mise en place d'un nouvel état de ligne pointillée
	 * @param type le nouveau type de ligne
	 */
	public void setEdgeType(LineType type)
	{
		edgeType = type;

		/*
		 * Il faut regénérer le stroke
		 * TODO Remplacer stroke par un nouveau stroke de la StrokeFactory
		 */
		stroke = null;
	}

	/**
	 * Initialisation d'une figure de type {@link #type} au point p et ajout de
	 * cette figure à la liste des {@link #figures}
	 * @param p le point où initialiser la figure
	 * @return la nouvelle figure créée à x et y avec les paramètres courants
	 */
	public Figure initiateFigure(Point2D p)
	{
		System.err.println(getClassName() + "::" + getMethodName()
		+ " not yet implemented");

		/*
		 * TODO Maintenant que l'on s'apprête effectivement à créer une figure on
		 * ajoute/obtient les Paints et le Stroke des factories
		 */

		/*
		 * TODO Obtention de la figure correspondant au type de figure choisi
		 * grâce à type.getFigure(...)
		 */
		Figure newFigure = null;

		/*
		 * TODO Ajout de la figure à #figures
		 */

		/* Notification des observers */
		update();

		return newFigure;
	}

	/**
	 * Obtention de la dernière figure (implicitement celle qui est en cours de
	 * dessin)
	 * @return la dernière figure du dessin
	 */
	public Figure getLastFigure()
	{
		/*
		 * TODO renvoyer la dernière figure des #figures (sous entendu
		 * celle qui vient d'être créée ou celle en cours de création)
		 */
		return null;
	}

	/**
	 * Obtention de la dernière figure contenant le point p.
	 * @param p le point sous lequel on cherche une figure
	 * @return une référence vers la dernière figure contenant le point p ou à
	 * défaut null.
	 */
	public Figure getFigureAt(Point2D p)
	{
		selectedFigure = null;

		/*
		 * TODO Recherche dans le flux des figures de la DERNIERE figure
		 * contenant le point p.
		 */

		return selectedFigure;
	}

	/**
	 * Retrait de la dernière figure.
	 * Utile pour retirer une figure de taille nulle lorsque l'on clique
	 * dans la zone de dessin
	 * @post le modèle de dessin a été mis à jour (update)
	 */
	public void removeLastFigure()
	{
		/*
		 * TODO retrait de la dernière figure
		 */
	}

	/**
	 * Effacement de toutes les figures (sera déclenché par une action clear)
	 * @post le modèle de dessin a été mis à jour
	 */
	public void clear()
	{
		/*
		 * TODO effacement de toutes les figures
		 */
	}

	/**
	 * Accesseur de l'état de filtrage
	 * @return l'état courant de filtrage
	 */
	public boolean getFiltering()
	{
		return filtering;
	}

	/**
	 * Changement d'état du filtrage
	 * @param filtering le nouveau statut de filtrage
	 * @post le modèle de dessin a été mis à jour
	 */
	public void setFiltering(boolean filtering)
	{
		this.filtering = filtering;
		update();
	}

	/**
	 * Ajout d'un filtre pour filtrer les types de figures
	 * @param filter le filtre à ajouter
	 * @return true si le filtre n'était pas déjà présent dans l'ensemble des
	 * filtres fitrant les types de figures, false sinon
	 * @post si le filtre a été ajouté, une mise à jour est déclenchée
	 */
	public boolean addShapeFilter(FigureFilter<FigureType> filter)
	{
		/*
		 * TODO Ajout de filter à shapeFilters s'il n'y est pas déja
		 */
		return false;
	}

	/**
	 * Retrait d'un filtre filtrant les types de figures
	 * @param filter le filtre à retirer
	 * @return true si le filtre faisait partie des filtres filtrant les types
	 * de figure et a été retiré, false sinon.
	 * @post si le filtre a été retiré, une mise à jour est déclenchée
	 */
	public boolean removeShapeFilter(FigureFilter<FigureType> filter)
	{
		/*
		 * TODO Retrait de filter à shapeFilters s'il en faisait partie
		 */
		return false;
	}

	/**
	 * Mise en place du filtre de couleur de remplissage
	 * @param filter le filtre de couleur de remplissage à appliquer
	 * @post le {@link #fillColorFilter} est mis en place et une mise à jour
	 * est déclenchée
	 */
	public void setFillColorFilter(FigureFilter<Paint> filter)
	{
		/*
		 * TODO Mise en place du fillColorFilter
		 */
	}

	/**
	 * Mise en place du filtre de couleur de trait
	 * @param filter le filtre de couleur de trait à appliquer
	 * @post le #edgeColorFilter est mis en place et une mise à jour
	 * est déclenchée
	 */
	public void setEdgeColorFilter(FigureFilter<Paint> filter)
	{
		/*
		 * TODO Mise en place du edgeColorFilter
		 */
	}

	/**
	 * Ajout d'un filtre pour filtrer les types de ligne des figures
	 * @param filter le filtre à ajouter
	 * @return true si le filtre n'était pas déjà présent dans l'ensemble des
	 * filtres fitrant les types de lignes, false sinon
	 * @post si le filtre a été ajouté, une mise à jour est déclenchée
	 */
	public boolean addLineFilter(FigureFilter<LineType> filter)
	{
		/*
		 * TODO Ajout de filter à lineFilters s'il n'y est pas déja
		 */
		return false;

	}

	/**
	 * Retrait d'un filtre filtrant les types de lignes
	 * @param filter le filtre à retirer
	 * @return true si le filtre faisait partie des filtres filtrant les types
	 * de lignes et a été retiré, false sinon.
	 * @post si le filtre a éré retiré, une mise à jour est déclenchée
	 */
	public boolean removeLineFilter(FigureFilter<LineType> filter)
	{
		/*
		 * TODO Retrait de filter à lineFilters s'il en faisait partie
		 */
		return false;
	}

	/**
	 * Remise à l'état non sélectionné de toutes les figures
	 */
	public void clearSelection()
	{
		/*
		 * TODO Remise à l'état non sélectionné de toutes les figures
		 */
	}

	/**
	 * Mise à jour des indices des figures sélectionnées dans
	 * {@link #selectionIndex} en parcourant l'ensemble des {@link #figures}
	 */
	public void updateSelection()
	{
		/*
		 * TODO Parcours ou stream des figures à la recherche de celles qui
		 * sont sélectionnées (isSelected) et mise à jour des indices des
		 * figures sélectionnées dans selectionIndex
		 */
		selectionIndex.clear();

		System.out.println(getClassName() + "::" + getMethodName()
		    + " Update Selection = " + selectionIndex);

		update();
	}

	/**
	 * Indique s'il existe des figures sélectionnées
	 * @return true s'il y a des figures sélectionnées
	 */
	public boolean hasSelection()
	{
		// TODO renvoyer vrai s'il y a des figures sélectionnées dans selectionIndex
		return false;
	}

	/**
	 * Destruction des figures sélectionnées.
	 * Et incidemment nettoyage de {@link #selectionIndex}
	 * @post Si au moins une figure a été retirée une mise à jour est déclenchée
	 */
	public void deleteSelected()
	{
		/*
		 * TODO Retrait des figures sélectionnées de #figures
		 * puis MAJ de la sélection (puisque ces figures n'existent plus)
		 * puis MAJ modèle
		 */
	}

	/**
	 * Applique un style particulier aux figure sélectionnées
	 * @param fill la couleur de remplissage à applique aux figures sélectionnées
	 * @param edge la couleur de trait à appliquer aux figures sélectionnées
	 * @param stroke le style de trait à appliquer aux figures sélectionnées
	 */
	public void applyStyleToSelected(Paint fill, Paint edge, BasicStroke stroke)
	{
		/*
		 * TODO Application des styles contenus dans fille, edge & stroke
		 * à l'ensemble des figures sélectionnées (répertoriés par selectionIndex
		 * pour aller plus vite)
		 * MAJ
		 */
	}

	/**
	 * Déplacement des figures sélectionnées en haut de la liste des figures.
	 * En conservant l'ordre des figures sélectionnées
	 * @post Si des figures ont été déplacées le modèle doit être mis à jour
	 */
	public void moveSelectedUp()
	{
		/*
		 * TODO Déplacement vers le début de la liste (#figures) des
		 * figures sélectionnées. Les figures ainsi déplacées seront dessinées
		 * avant les autres.
		 * note: il sera sans doute plus facile de recréer une nouvelle
		 * collection de figures, puis de l'échanger avec #figures
		 * MAJ de la sélection et du modèle
		 */
	}

	/**
	 * Déplacement des figures sélectionnées en bas de la liste des figures.
	 * En conservant l'ordre des figures sélectionnées
	 */
	public void moveSelectedDown()
	{
		/*
		 * TODO Déplacement vers la fin de la liste (#figures) des
		 * figures sélectionnées. Les figures ainsi déplacées seront dessinées
		 * après les autres.
		 * note: il sera sans doute plus facile de recréer une nouvelle
		 * collection de figures, puis de l'échanger avec #figures
		 * MAJ de la sélection et du modèle
		 */
	}

	/**
	 * Accès aux figures dans un stream afin que l'on puisse y appliquer
	 * de filtres
	 * @return le flux des figures éventuellement filtrés par les différents
	 * filtres
	 */
	public Stream<Figure> stream()
	{
		Stream<Figure> figuresStream = figures.stream();
		if (filtering)
		{
			// TODO Filtrez le flux de figures avec "shapeFilters" s'il est non vide

			// TODO Filtrez le flux de figures avec "fillColorFilter" s'il est non null

			// TODO Filtrez le flux de figures avec "edgeColorFilter" s'il est non null

			// TODO Filtrez le flux de figures avec "lineFilters" s'il est non vide
		}

		return figuresStream;
	}

	/* (non-Javadoc)
	 * @see history.Originator#createMemento()
	 */
	@Override
	public Memento<Figure> createMemento()
	{
		/*
		 * TODO Création d'un memento contenant l'ensemble des figures à dessiner
		 */
		return null;
	}

	/* (non-Javadoc)
	 * @see history.Originator#setMemento(history.Memento)
	 */
	@Override
	public void setMemento(Memento<Figure> memento)
	{
		/*
		 * TODO Mise en place d'un memento contenant des figures pour remplacer
		 * l'ensemble de figures à dessiner
		 * MAJ
		 */
	}
}
