package figures;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import figures.enums.FigureType;
import utils.Signature;

/**
 * Classe de Ellipse pour les {@link Figure}
 * @author davidroussel
 * @uml.dependency supplier="java.awt.geom.Ellipse2D.Double"
 */
public class Ellipse extends Figure implements Signature
{
	/**
	 * Le compteur d'instance des ellipses. Utilisé pour donner un numéro
	 * d'instance après l'avoir incrémenté
	 */
	private static int counter = 0;

	/**
	 * Création d'un ellipse avec les points en haut à gauche et en bas à droite
	 * @param stroke le type de trait
	 * @param edge la couleur du trait
	 * @param fill la couleur de remplissage
	 * @param topLeft le point en haut à gauche
	 * @param bottomRight le point en bas à droite
	 */
	public Ellipse(BasicStroke stroke,
	               Paint edge,
	               Paint fill,
	               Point2D topLeft,
	               Point2D bottomRight)
	{
		super(stroke, edge, fill);
		instanceNumber = ++counter;
		double x = topLeft.getX();
		double y = topLeft.getY();
		double w = bottomRight.getX() - x;
		double h = bottomRight.getY() - y;
		shape = new Ellipse2D.Double(x, y, w, h);
	}

	/**
	 * Constructeur de copie assurant une copie distincte de l'ellipse
	 * @param ellipse l'ellipse à copier
	 */
	public Ellipse(Ellipse ellipse)
	{
		super(ellipse);
		Ellipse2D oldEllipse = (Ellipse2D) ellipse.shape;
		shape = new Ellipse2D.Double(oldEllipse.getMinX(),
		                             oldEllipse.getMinY(),
		                             oldEllipse.getWidth(),
		                             oldEllipse.getHeight());
	}

	/**
	 * Création d'une copie distincte de la figure
	 * @see figures.Figure#clone()
	 */
	@Override
	public Figure clone()
	{
		return new Ellipse(this);
	}

	/**
	 * Comparaison de deux figures
	 * @param Object o l'objet à comparer
	 * @return true si obj est une figure de même type et que son contenu est
	 * identique
	 */
	@Override
	public boolean equals(Object o)
	{
		if (super.equals(o))
		{
			Ellipse e = (Ellipse) o;
			return ((Ellipse2D.Double) shape).equals(e.shape);
		}
		return false;
	}

	/**
	 * Déplacement du point inférieur droit de l'ellipse
	 * @param p le point où placer le dernier point (point inférieur droit)
	 * @see figures.Figure#setLastPoint(Point2D)
	 */
	@Override
	public void setLastPoint(Point2D p)
	{
		if (shape != null)
		{
			Ellipse2D.Double ellipse = (Ellipse2D.Double) shape;
			double newWidth = p.getX() - ellipse.x;
			double newHeight = p.getY() - ellipse.y;
			ellipse.width = newWidth;
			ellipse.height = newHeight;
		}
		else
		{
			System.err.println(getClassName() + "::" + getMethodName()
			    + " null shape");
		}
	}

	/**
	 * Obtention du barycentre de la figure.
	 * @return le point correspondant au barycentre de la figure
	 */
	@Override
	public Point2D getCenter()
	{
		Ellipse2D.Double ellipse = (Ellipse2D.Double) shape;
		Point2D center = new Point2D.Double(ellipse.getCenterX(),
		                                    ellipse.getCenterY());
		Point2D tCenter = new Point2D.Double();
		getTransform().transform(center, tCenter);

		return tCenter;
	}

	/**
	 * Normalise une figure de manière à exprimer tous ses points par rapport
	 * à son centre, puis transfère la position réelle du centre dans l'attribut
	 * {@link #translation}
	 */
	@Override
	public void normalize()
	{
		Point2D center = getCenter();
		double cx = center.getX();
		double cy = center.getY();
		Ellipse2D.Double ellipse = (Ellipse2D.Double) shape;
		translation.translate(cx, cy);
		ellipse.setFrame(ellipse.x - cx,
		                 ellipse.y - cy,
		                 ellipse.width,
		                 ellipse.height);
	}

	/**
	 * Accesseur du type de figure selon {@link FigureType}
	 * @return le type de figure
	 */
	@Override
	public FigureType getType()
	{
		return FigureType.ELLIPSE;
	}
}
