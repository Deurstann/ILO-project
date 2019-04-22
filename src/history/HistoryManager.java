package history;

import utils.Signature;

/**
 * Classe permettant de gérer les piles de Undo et de Redo de E
 * @param E l'état à sauvegarder dans les piles
 * @author davidroussel
 */
public class HistoryManager<E extends Prototype<E>> implements Signature
{
	/**
	 * Le nombre maximum d'undo / redo
	 */
	private int size;

	/**
	 * L'originator dont on doit sauvegarder l'état.
	 * Permet de demander à l'originator de générer un memento ou de
	 * mettre en place un memento qu'on lui fournit
	 */
	private Originator<E> originator;

	/*
	 * TODO Compléter les attributs par les piles de undo et de redo
	 */

	/**
	 * Constructeur du manager de Undo/Redo
	 * @param origin l'originator dont on doit savegarder l'état
	 * @param size ne nombre maximum d'undos/redos à mémorsier
	 */
	public HistoryManager(Originator<E> origin, int size)
	{
		this.size = size;
		originator = origin;
		// TODO Compléter ...
	}

	@Override
	protected void finalize() throws Throwable
	{
		// TODO Compléter ...
		super.finalize();
	}

	/**
	 * Nombre d'éléments accumulés dans la pile de undo
	 * @return Le nombre d'éléments accumulés dans la pile de undo
	 */
	public int undoSize()
	{
		// TODO Remplacer par l'implémentation ...
		return 0;
	}

	/**
	 * Nombre d'éléments accumulés dans la pile de redo
	 * @return Le nombre d'éléments accumulés dans la pile de redo
	 */
	public int redoSize()
	{
		// TODO Remplacer par l'implémentation ...
		return 0;
	}

	/**
	 * Enregistre un {@link Memento} de l'{@link #originator} pour pouvoir
	 * le restituer par la suite.
	 */
	public void record()
	{
		// TODO Compléter ...
	}

	/**
	 * Restitue le dernier Memento sauvegardé dans la pile des undo
	 * @return le dernier memento sauvegardé dans la pile des undo
	 * ({@link #undoStack}), ou bien null si celle-ci est vide.
	 * @post un {@link Memento} de l'{@link #originator} a été créé au préalable
	 * dans la pile des redo.
	 */
	public void undo()
	{
		// TODO Compléter ...
	}

	/**
	 * Annule le dernier {@link Memento} enregistré dans la pile des undo.
	 * Lorsque l'action n'a pas modifié l'état (par exemple si elle a échoué)
	 */
	public void cancel()
	{
		// TODO Compléter ...
	}

	/**
	 * Restitue de dernier Memento sauvegardé dans la pile des redo
	 * @return Le dernier Memento sauvegardé dans la pile des redo
	 * ({@link #redoStack}) ou bien null si celle-ci est vide.
	 * @post un {@link Memento} de l'{@link #originator} a été créé au préalable
	 * dans la pile des undo.
	 */
	public void redo()
	{
		// TODO Compléter ...
	}

	/**
	 * Affichage du contenu des piles {@link #undoStack} et {@link #redoStack}
	 * @return Une chaîne permettant d'afficher le contenu des piles (utile
	 * pour débuguer)
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(super.toString());
		sb.append("[" + String.valueOf(size) + "] :\nUndo = {");
		// TODO Compléter ...
		sb.append("},\nRedo = {");
		// TODO Compléter ...
		sb.append("}");
		return sb.toString();
	}
}
