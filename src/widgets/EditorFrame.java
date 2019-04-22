package widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

import figures.Drawing;
import figures.Figure;
import figures.enums.FigureType;
import figures.enums.LineType;
import figures.listeners.AbstractFigureListener;
import figures.listeners.creation.AbstractCreationListener;
import figures.listeners.transform.AbstractTransformShapeListener;
import history.HistoryManager;
import utils.IconFactory;
import utils.PaintFactory;
import utils.Signature;
import widgets.enums.OperationMode;

/**
 * Classe de la fenêtre principale de l'éditeur de figures
 * @author davidroussel
 */
@SuppressWarnings("serial")
public class EditorFrame extends JFrame implements Signature
{
	/**
	 * Le nom de l'éditeur
	 */
	protected static final String EditorName = "Figure Editor v5.2";

	/**
	 * Le modèle de dessin sous-jacent;
	 */
	protected Drawing drawingModel;

	/**
	 * Le gestionnaire d'historique pour les Undo/Redo
	 */
	protected HistoryManager<Figure> history;

	/**
	 * Taille de l'historique
	 */
	protected static final int historyLength = 32;

	/**
	 * Indique si l'éditeur est en mode Création de figures ou édition
	 * de figures (mode initial : création de figures)
	 */
	protected OperationMode operationMode = OperationMode.CREATION;

	/**
	 * La zone de dessin dans laquelle seront dessinées les figures.
	 * On a besoin d'une référence à la zone de dessin (contrairement aux
	 * autres widgets) car il faut lui affecter un xxxCreationListener en
	 * fonction de la figure choisie dans la liste des figures possibles.
	 */
	protected DrawingPanel drawingPanel;

	/**
	 * Le creationListener à mettre en place dans le drawingPanel en fonction
	 * du type de figure choisie;
	 */
	protected AbstractCreationListener creationListener;

	/**
	 * Le listener à mettre en place dans le drawingPanel lorsque l'on
	 * est en mode édition de figures pour déplacer les figures sélectionnées
	 */
	protected AbstractTransformShapeListener moveListener;

	/**
	 * Le listener à mettre en place dans le drawingPanel lorsque l'on
	 * est en mode édition de figures pour faire tourner les figures
	 * sélectionnées
	 */
	protected AbstractTransformShapeListener rotateListener;

	/**
	 * Le listener à mettre en place dans le drawingPanel lorsque l'on
	 * est en mode édition de figures pour changer l'échelle les figures
	 * sélectionnées
	 */
	protected AbstractTransformShapeListener scaleListener;

	/**
	 * Le listener de sélection des figures à mettre en place lorsque l'on
	 * est en mode édition.
	 */
	protected AbstractFigureListener selectionListener;

	/**
	 * Le label dans la barre d'état en bas dans lequel on affiche les
	 * conseils utilisateur pour créer une figure
	 */
	protected JLabel infoLabel;

	/**
	 * L'index de l'élément sélectionné par défaut pour le type de figure
	 */
	private final static int defaultFigureTypeIndex = FigureType.ELLIPSE.intValue();

	/**
	 * Les noms des couleurs de remplissage à utiliser pour remplir
	 * la [labeled]combobox des couleurs de remplissage
	 */
	protected final static String[] fillColorNames =
	    { "Black", "White", "Red", "Orange", "Yellow", "Green", "Cyan", "Blue",
	        "Magenta", "Others", "None" };

	/**
	 * Les couleurs de remplissage à utiliser en fonction de l'élément
	 * sélectionné dans la [labeled]combobox des couleurs de remplissage
	 */
	protected final static Paint[] fillPaints =
	    { Color.black, Color.white, Color.red, Color.orange, Color.yellow,
	        Color.green, Color.cyan, Color.blue, Color.magenta, null, // Color
	                                                                  // selected
	                                                                  // by a
	                                                                  // JColorChooser
	        null // No Color
		};

	/**
	 * L'index de l'élément sélectionné par défaut dans les couleurs de
	 * remplissage
	 */
	private final static int defaultFillColorIndex = 0; // black

	/**
	 * L'index de la couleur de remplissage à choisir avec un
	 * {@link JColorChooser} fournit par la {@link PaintFactory}
	 */
	private final static int specialFillColorIndex = 9;

	/**
	 * Les noms des couleurs de trait à utiliser pour remplir
	 * la [labeled]combobox des couleurs de trait
	 */
	protected final static String[] edgeColorNames = { "Magenta", "Red",
	    "Orange", "Yellow", "Green", "Cyan", "Blue", "Black", "Others" };

	/**
	 * Les couleurs de trait à utiliser en fonction de l'élément
	 * sélectionné dans la [labeled]combobox des couleurs de trait
	 */
	protected final static Paint[] edgePaints =
	    { Color.magenta, Color.red, Color.orange, Color.yellow, Color.green,
	        Color.cyan, Color.blue, Color.black, null // Color selected by a
	                                                  // JColorChooser
		};

	/**
	 * L'index de l'élément sélectionné par défaut dans les couleurs de
	 * trait
	 */
	private final static int defaultEdgeColorIndex = 6; // blue;

	/**
	 * L'index de la couleur de remplissage à choisir avec un
	 * {@link JColorChooser} fournit par la {@link PaintFactory}
	 */
	private final static int specialEdgeColorIndex = 8;

	/**
	 * L'index de l'élément sélectionné par défaut dans les types de
	 * trait
	 */
	private final static int defaultEdgeTypeIndex = 1; // solid

	/**
	 * La largeur de trait par défaut
	 */
	private final static int defaultEdgeWidth = 4;

	/**
	 * Largeur de trait minimum
	 */
	private final static int minEdgeWidth = 1;

	/**
	 * Largeur de trait maximum
	 */
	private final static int maxEdgeWidth = 30;

	/**
	 * l'incrément entre deux largeurs de trait
	 */
	private final static int stepEdgeWidth = 1;

	/**
	 * Action déclenchée lorsque l'on clique sur le bouton quit ou sur l'item
	 * de menu quit
	 */
	private final Action quitAction = new QuitAction();

	/**
	 * Action déclenchée lorsque l'on clique sur le bouton undo ou sur l'item
	 * de menu undo
	 */
	private final Action undoAction = new UndoAction();

	/**
	 * Action réalisée lorsque l'on souhaite refaire une action qui vient
	 * d'être annulée
	 */
	private final Action redoAction = new RedoAction();

	/**
	 * Action déclenchée lorsque l'on clique sur le bouton clear ou sur l'item
	 * de menu clear
	 */
	private final Action clearAction = new ClearAction();

	/**
	 * Action déclenchée lorsque l'on clique sur le bouton about ou sur l'item
	 * de menu about
	 */
	private final Action aboutAction = new AboutAction();

	/**
	 * Action déclenchée lorsque l'on sélectionne de mode édition des figures
	 */
	private final Action toggleCreateEditAction = new ToggleCreateEditAction();

	/**
	 * Action déclenchée pour mettre filter ou non les figures
	 */
	private final Action filterAction = new FilterAction();

	/**
	 * Action déclenchée lorsque l'on clique sur l'item de menu de filtrage
	 * des cercles
	 */
	private final Action circleFilterAction =
	    new ShapeFilterAction(FigureType.CIRCLE);

	/**
	 * Action déclenchée lorsque l'on clique sur l'item de menu de filtrage
	 * des ellipses
	 */
	private final Action ellipseFilterAction =
	    new ShapeFilterAction(FigureType.ELLIPSE);

	/**
	 * Action déclenchée lorsque l'on clique sur l'item de menu de filtrage
	 * des rectangles
	 */
	private final Action rectangleFilterAction =
	    new ShapeFilterAction(FigureType.RECTANGLE);

	/**
	 * Action déclenchée lorsque l'on clique sur l'item de menu de filtrage
	 * des rectangles arrondis
	 */
	private final Action rRectangleFilterAction =
	    new ShapeFilterAction(FigureType.ROUNDED_RECTANGLE);

	/**
	 * Action déclenchée lorsque l'on clique sur l'item de menu de filtrage
	 * des polygones
	 */
	private final Action polyFilterAction =
	    new ShapeFilterAction(FigureType.POLYGON);

	/**
	 * Action déclenchée lorsque l'on clique sur l'item de menu de filtrage
	 * des polygones réguliers
	 */
	private final Action ngonFilterAction =
	    new ShapeFilterAction(FigureType.NGON);

	/**
	 * Action déclenchée lorsque l'on clique sur l'item de menu de filtrage
	 * des étoiles
	 */
	private final Action starFilterAction =
	    new ShapeFilterAction(FigureType.STAR);

	/**
	 * Action déclenchée lorsque l'on clique sur l'item de menu de filtrage
	 * des type de lignes vides
	 */
	private final Action noneLineFilterAction =
	    new LineFilterAction(LineType.NONE);

	/**
	 * Action déclenchée lorsque l'on clique sur l'item de menu de filtrage
	 * des type de lignes pleines
	 */
	private final Action solidLineFilterAction =
	    new LineFilterAction(LineType.SOLID);

	/**
	 * Action déclenchée lorsque l'on clique sur l'item de menu de filtrage
	 * des type de lignes pointillées
	 */
	private final Action dashedLineFilterAction =
	    new LineFilterAction(LineType.DASHED);

	/**
	 * Action déclenchée pour filter ou non les figures suivant
	 * la couleur de replissage courante
	 */
	private final Action fillColorFilterAction = new FillColorFilterAction();

	/**
	 * Action déclenchée pour filter ou non les figures suivant
	 * la couleur de trait courante
	 */
	private final Action edgeColorFilterAction = new EdgeColorFilterAction();

	/**
	 * Action réalisée pour détruire les figures sélectionnées
	 */
	private final Action deleteAction = new DeleteAction();

	/**
	 * Action réalisée pour monter les figures sélectionnées en tête de liste
	 * des figures
	 */
	private final Action moveUpAction = new MoveUpAction();

	/**
	 * Action réalisée pour descendre les figures sélectionnées en fin de liste
	 * des figures
	 */
	private final Action moveDownAction = new MoveDownAction();

	/**
	 * Action réalisée pour appliquer le style courant (couleur de remplissage,
	 * couleur de trait et style de trait) aux figures sélectionnées
	 */
	private final Action styleAction = new StyleAction();

	/**
	 * Action permettant de dessiner l'ensemble des figures afin de
	 * montrer leurs possibilités
	 */
	private final Action magicDrawAction = new MagicDrawAction();

	/**
	 * Constructeur de la fenètre de l'éditeur.
	 * Construit les widgets et assigne les actions et autres listeners
	 * aux widgets
	 * @throws HeadlessException
	 */
	public EditorFrame() throws HeadlessException
	{
		drawingModel = new Drawing();
		history = new HistoryManager<Figure>(drawingModel, historyLength);
		operationMode = OperationMode.CREATION;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		boolean isMacOS = System.getProperty("os.name").startsWith("Mac OS");

		/*
		 * Construire l'interface graphique en utilisant WindowBuilder:
		 * Menu Contextuel -> Open With -> WindowBuilder Editor puis
		 * aller dans l'onglet Design
		 */
		setPreferredSize(new Dimension(650, 450));
		creationListener = null;

		setTitle("Figure Editor v5.2");
		if (!isMacOS)
		{
			URL path = EditorFrame.class.getResource("/images/Logo.png");
			if (path != null)
			{
				setIconImage(Toolkit.getDefaultToolkit().getImage(path));
			}
			else
			{
				System.err.println(getClassName() + "::" + getMethodName()
				    + "() : Could not find /images/Logo.png");
			}
		}

		// --------------------------------------------------------------------
		// Toolbar en haut
		// --------------------------------------------------------------------
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		getContentPane().add(toolBar, BorderLayout.NORTH);

		JToggleButton tglbtnMove = new JToggleButton("Move");
		tglbtnMove.setAction(toggleCreateEditAction);
		((ToggleCreateEditAction) toggleCreateEditAction)
		    .registerButton(tglbtnMove);
		toolBar.add(tglbtnMove);

		Component toolBoxSpringer = Box.createHorizontalGlue();
		toolBar.add(toolBoxSpringer);

		JButton btnClose = new JButton("Close");
		btnClose.setAction(quitAction);
		toolBar.add(btnClose);

		// --------------------------------------------------------------------
		// Barre d'état en bas
		// --------------------------------------------------------------------
		JPanel bottomPanel = new JPanel();
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));

		infoLabel = new JLabel(AbstractFigureListener.defaultTip);
		bottomPanel.add(infoLabel);

		Component horizontalGlue = Box.createHorizontalGlue();
		bottomPanel.add(horizontalGlue);

		JLabel coordsLabel = new JLabel(DrawingPanel.defaultCoordString);
		bottomPanel.add(coordsLabel);

		// --------------------------------------------------------------------
		// Panneau de contrôle à gauche
		// --------------------------------------------------------------------
		JPanel leftPanel = new JPanel();
		leftPanel.setPreferredSize(new Dimension(220, 10));
		leftPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		getContentPane().add(leftPanel, BorderLayout.WEST);

		JLabeledComboBox figureTypeCombobox =
		    new JLabeledComboBox("Shape",
		                         FigureType.stringValues(),
		                         defaultFigureTypeIndex,
		                         (ItemListener) null);
		figureTypeCombobox.setAlignmentX(Component.CENTER_ALIGNMENT);
		figureTypeCombobox.setPreferredSize(new Dimension(80, 32));
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.add(figureTypeCombobox);

		/*
		 * TODO Vous pourrez utiliser ce spinner model dans le spinner
		 * gérant l'épaisseur de trait
		 */
		SpinnerNumberModel snm = new SpinnerNumberModel(defaultEdgeWidth,
		                                                minEdgeWidth,
		                                                maxEdgeWidth,
		                                                stepEdgeWidth);

		InfoPanel infoPanel = new InfoPanel();
		leftPanel.add(infoPanel);
		infoPanel.setAlignmentY(Component.TOP_ALIGNMENT);

		// --------------------------------------------------------------------
		// Zone de dessin
		// --------------------------------------------------------------------
		drawingPanel = new DrawingPanel(drawingModel, coordsLabel, infoPanel);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setWheelScrollingEnabled(false);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(drawingPanel);

		// --------------------------------------------------------------------
		// Barre de menus
		// --------------------------------------------------------------------
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menuFile = new JMenu("Drawing");
		menuBar.add(menuFile);

		JMenuItem mntmMagicDraw = new JMenuItem("MagicDraw");
		mntmMagicDraw.setAction(magicDrawAction);
		menuFile.add(mntmMagicDraw);

		JMenu menuEdition = new JMenu("Edition");
		menuBar.add(menuEdition);

		JCheckBoxMenuItem chckbxmntmEdit = new JCheckBoxMenuItem("Edit");
		chckbxmntmEdit.setAction(toggleCreateEditAction);
		((ToggleCreateEditAction) toggleCreateEditAction)
		    .registerButton(chckbxmntmEdit);
		menuEdition.add(chckbxmntmEdit);

		JMenu menuFilter = new JMenu("Filter");
		menuBar.add(menuFilter);

		JCheckBoxMenuItem chckbxmntmFiltering =
		    new JCheckBoxMenuItem("Filtering");
		chckbxmntmFiltering.setAction(filterAction);
		menuFilter.add(chckbxmntmFiltering);

		JMenu menuFigures = new JMenu("Figures");
		menuFilter.add(menuFigures);

		JCheckBoxMenuItem chckbxmntmEllipses =
		    new JCheckBoxMenuItem("Ellipses");
		chckbxmntmEllipses.setAction(ellipseFilterAction);
		menuFigures.add(chckbxmntmEllipses);

		JMenu menuColors = new JMenu("Colors");
		menuFilter.add(menuColors);

		JMenu menuStrokes = new JMenu("Strokes");
		menuFilter.add(menuStrokes);

		JSeparator separator = new JSeparator();
		menuFile.add(separator);

		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.setAction(quitAction);
		menuFile.add(mntmQuit);

		JMenu menuHelp = new JMenu("Help");
		menuBar.add(menuHelp);

		JMenuItem mntmAbout = new JMenuItem("About...");
		mntmAbout.setAction(aboutAction);
		menuHelp.add(mntmAbout);

		// --------------------------------------------------------------------
		// Ajout des contrôleurs aux widgets
		// pour connaître les Listeners applicable à un widget
		// dans WindowBuilder, sélectionnez un widger de l'UI puis Menu
		// Contextuel -> Add event handler
		// --------------------------------------------------------------------
		// TODO Créer une classe MoveShapeListener
		moveListener = null;
		// TODO Créer une classe ScaleShapeListener
		scaleListener = null;
		// TODO Créer une classe RotateShapeListener
		rotateListener = null;
		// TODO Créer une classe SelectionFigureListener
		selectionListener = null;

		figureTypeCombobox.addItemListener(new ShapeItemListener(FigureType
		    .fromInteger(figureTypeCombobox.getSelectedIndex())));
	}

	/**
	 * Action pour quitter l'application
	 * @author davidroussel
	 */
	private class QuitAction extends AbstractAction // implements QuitHandler
	{
		/**
		 * Constructeur de l'action pour quitter l'application.
		 * Met en place le raccourci clavier, l'icône et la description
		 * de l'action
		 */
		public QuitAction()
		{
			putValue(NAME, "Quit");
			/*
			 * Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
			 * = InputEvent.CTRL_MASK on win/linux
			 * = InputEvent.META_MASK on mac os
			 */
			putValue(ACCELERATOR_KEY,
			         KeyStroke.getKeyStroke(KeyEvent.VK_Q,
			                                Toolkit.getDefaultToolkit()
			                                    .getMenuShortcutKeyMask()));
			putValue(LARGE_ICON_KEY, IconFactory.getIcon("Quit"));
			putValue(SMALL_ICON, IconFactory.getIcon("Quit_small"));
			putValue(SHORT_DESCRIPTION, "Quits the application");
		}

		/**
		 * Opérations réalisées par l'action : Quitte l'application
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 * ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			doQuit();
		}

		/**
		 * Action réalisée pour quitter dans un {@link Action}
		 */
		private void doQuit()
		{
			/*
			 * Action à effectuer lorsque l'action "undo" est cliquée :
			 * TODO sortir avec un System.exit() (pas très propre, mais fonctionne)
			 */
		}
	}

	/**
	 * Action réalisée pour effacer la dernière action dans le dessin
	 */
	private class UndoAction extends AbstractAction
	{
		/**
		 * Constructeur de l'action effacer la dernière action sur le dessin
		 * Met en place le raccourci clavier, l'icône et la description
		 * de l'action
		 */
		public UndoAction()
		{
			putValue(NAME, "Undo");
			putValue(ACCELERATOR_KEY,
			         KeyStroke.getKeyStroke(KeyEvent.VK_Z,
			                                Toolkit.getDefaultToolkit()
			                                    .getMenuShortcutKeyMask()));
			putValue(LARGE_ICON_KEY, IconFactory.getIcon("Undo"));
			putValue(SMALL_ICON, IconFactory.getIcon("Undo_small"));
			putValue(SHORT_DESCRIPTION, "Undo last drawing");
		}

		/**
		 * Opérations réalisées par l'action : Mise en place du dernier
		 * Memento enregistré dans la pile des undo
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 * ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// TODO Compléter ...
		}
	}

	/**
	 * Action réalisée pour refaire la dernière action (qui a été annulée)
	 * dans le dessin
	 */
	private class RedoAction extends AbstractAction
	{
		public RedoAction()
		{
			putValue(NAME, "Redo");
			putValue(LARGE_ICON_KEY, IconFactory.getIcon("Redo"));
			putValue(SMALL_ICON, IconFactory.getIcon("Redo_small"));
			putValue(ACCELERATOR_KEY,
			         KeyStroke.getKeyStroke(KeyEvent.VK_Z,
			                                InputEvent.SHIFT_MASK
			                                    | Toolkit.getDefaultToolkit()
			                                        .getMenuShortcutKeyMask()));
			putValue(SHORT_DESCRIPTION, "Redo last drawing");
		}

		/**
		 * Opérations réalisées par l'action : Mise en place du dernier
		 * Memento enregistré dans la pile des redo
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 * ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// TODO Compléter ...
		}
	}

	/**
	 * Action réalisée pour effacer toutes les figures du dessin
	 */
	private class ClearAction extends AbstractAction
	{
		/**
		 * Constructeur de l'action pour effacer toutes les figures du dessin
		 * Met en place le raccourci clavier, l'icône et la description
		 * de l'action
		 */
		public ClearAction()
		{
			putValue(NAME, "Clear");
			putValue(ACCELERATOR_KEY,
			         KeyStroke.getKeyStroke(KeyEvent.VK_X,
			                                Toolkit.getDefaultToolkit()
			                                    .getMenuShortcutKeyMask()));
			putValue(LARGE_ICON_KEY, IconFactory.getIcon("Clear"));
			putValue(SMALL_ICON, IconFactory.getIcon("Clear_small"));
			putValue(SHORT_DESCRIPTION, "Erase all drawings");
		}

		/**
		 * Opérations réalisées par l'action : Effacement de toutes les figures
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 * ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			/*
			 * Action à effectuer lorsque l'action "clear" est cliquée :
			 * TODO Effacer toutes les figures du dessin
			 * Pro-tip : ne pas oublier d'enregistrer l'état actuel afin de
			 * pouvoir le restaurer avec un undo
			 */
		}
	}

	/**
	 * Action réalisée pour afficher la boite de dialogue "A propos ..."
	 */
	private class AboutAction extends AbstractAction // implements AboutHandler
	{
		/**
		 * Constructeur de l'action pour afficher la boite de dialogue
		 * "A propos ..." Met en place le raccourci clavier, l'icône et la
		 * description de l'action
		 */
		public AboutAction()
		{
			putValue(ACCELERATOR_KEY,
			         KeyStroke.getKeyStroke(KeyEvent.VK_I,
			                                Toolkit.getDefaultToolkit()
			                                    .getMenuShortcutKeyMask()));
			putValue(LARGE_ICON_KEY, IconFactory.getIcon("About"));
			putValue(SMALL_ICON, IconFactory.getIcon("About_small"));
			putValue(NAME, "About");
			putValue(SHORT_DESCRIPTION, "App information");
		}

		/**
		 * Opérations réalisées par l'action : Affichage d'une boite de dialogue
		 * Affichant des infos sur l'application
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 * ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			doAbout(e);
		}

		/**
		 * Action réalisée pour "A propos" dans un {@link Action}
		 * @param e l'évènement ayant déclenché l'action
		 */
		private void doAbout(EventObject e)
		{
			/*
			 * Action à effectuer lorsque l'action "about" est cliquée :
			 * TODO Ouvrir un MessageDialog (JOptionPane.showMessageDialog(...)) de
			 * type JOptionPane.INFORMATION_MESSAGE avec comme message l'EditorName
			 */
			Object source = e.getSource();
			// TODO  ...
		}
	}

	/**
	 * Action réalisée lorsque l'on passe en mode édition des figures
	 */
	private class ToggleCreateEditAction extends AbstractAction
	{
		/**
		 * Liste des "boutons" pouvant déclencher cette action.
		 * De manière à ce que lorqu'un bouton déclenche l'action
		 * les autres boutons soient eux aussi mis dans l'état correspondant
		 * à l'action.
		 * Ceci permet de déclencher/enclencher cette action depuis un bouton
		 * dana la barre d'outils et/ou depuis un item de menu dans les menus
		 */
		private List<AbstractButton> buttons;

		/**
		 * Constructeur de l'action pour mettre en place ou enlever un filtre
		 * pour filtrer les types de figures
		 */
		public ToggleCreateEditAction()
		{
			putValue(ACCELERATOR_KEY,
			         KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
			                                InputEvent.ALT_MASK));
			putValue(NAME, "Edition");
			putValue(LARGE_ICON_KEY, IconFactory.getIcon("Edition"));
			putValue(SMALL_ICON, IconFactory.getIcon("Edition_small"));
			putValue(SHORT_DESCRIPTION, "Édition des figures");

			buttons = new ArrayList<AbstractButton>();
		}

		/**
		 * Ajout d'un bouton déclenchant cette action
		 * @param button le bouton à ajouter à la liste des boutons
		 * @return true si le bouton a été ajouté à la liste des boutons
		 * déclenchant cette action, false si le bouton était déjà présent
		 * dans la liste des actions et n'a pas été ajouté
		 */
		public boolean registerButton(AbstractButton button)
		{
			if (!buttons.contains(button))
			{
				return buttons.add(button);
			}
			return false;
		}

		/**
		 * Opérations réalisées par l'action : Changement de mode Création /
		 * Edition des figures
		 * @param event l'évènement déclenchant l'action. Peut provenir d'un
		 * bouton ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent event)
		{
			AbstractButton button = (AbstractButton) event.getSource();
			boolean selected = button.getModel().isSelected();

			// TODO Parcours des "buttons" pour s'assurer qu'ils soient identiques

			if (selected)
			{
				operationMode = OperationMode.TRANSFORMATION;
				/*
				 * TODO
				 * 	- enlever le creationListener du drawingPanel
				 * 	- ajouter au drawingPanel les
				 * 		- selectionListener pour sélectionner/déselectionner des figures
				 * 		- moveListener pour déplacer les figures
				 * 		- rotateListenener pour faire tourner des figures
				 * 		- scaleListener pour changer la taille de figures
				 */
			}
			else
			{
				operationMode = OperationMode.CREATION;
				/*
				 * TODO
				 * 	- ajouter le creationListener du drawingPanel
				 * 	- enlever du drawingPanel les
				 * 		- selectionListener
				 * 		- moveListener
				 * 		- rotateListenener
				 * 		- scaleListener
				 */
			}
		}
	}

	/**
	 * Action réalisée pour filtrer ou pas le flux de figures
	 */
	private class FilterAction extends AbstractAction
	{
		/**
		 * Constructeur de l'action pour mettre en place ou enlever un filtre
		 * pour filtrer les types de figures
		 */
		public FilterAction()
		{
			putValue(NAME, "Filter");
			putValue(LARGE_ICON_KEY, IconFactory.getIcon("Filter"));
			putValue(SMALL_ICON, IconFactory.getIcon("Filter_small"));
			putValue(SHORT_DESCRIPTION, "Set/unset filtering");
			putValue(ACCELERATOR_KEY,
			         KeyStroke.getKeyStroke(KeyEvent.VK_F,
			                                Toolkit.getDefaultToolkit()
			                                    .getMenuShortcutKeyMask()));

		}

		/**
		 * Opérations réalisées par l'action : Mise en place ou arrêt du
		 * filtrage des figures
		 * @param event l'évènement déclenchant l'action. Peut provenir d'un
		 * bouton ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent event)
		{
			AbstractButton button = (AbstractButton) event.getSource();
			boolean selected = button.getModel().isSelected();
			// TODO Mettre en place le filtrage sélectionné dans le drawingModel
		}
	}

	/**
	 * Action réalisée pour ajouter ou retirer un filtre de type de figure
	 */
	private class ShapeFilterAction extends AbstractAction
	{
		/**
		 * Le type de figure
		 */
		private FigureType type;

		/**
		 * Constructeur de l'action pour mettre en place ou enlever un filtre
		 * pour filtrer les types de figures
		 */
		public ShapeFilterAction(FigureType type)
		{
			this.type = type;
			String name = type.toString();
			putValue(LARGE_ICON_KEY, IconFactory.getIcon(name));
			putValue(SMALL_ICON, IconFactory.getIcon(name + "_small"));
			putValue(NAME, name);
			putValue(SHORT_DESCRIPTION, "Set/unset " + name + " filter");
		}

		/**
		 * Opérations réalisées par l'action : Ajout ou retrait d'un filtre
		 * concernant un type particulier de figure ({@link #type})
		 * @param event l'évènement déclenchant l'action. Peut provenir d'un
		 * bouton ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent event)
		{
			AbstractButton button = (AbstractButton) event.getSource();
			boolean selected = button.getModel().isSelected();

			/*
			 * TODO Créer un new ShapeFilter correspondant au "type" de cette action
			 * puis l'ajouter ou bien le retirer du drawingModel suivant la
			 * sélection du bouton
			 */
		}
	}

	/**
	 * Action réalisée pour ajouter ou retirer un filtre de type trait de figure
	 */
	private class LineFilterAction extends AbstractAction
	{
		/**
		 * Le type de trait de la figure
		 */
		private LineType type;

		/**
		 * Constructeur de l'action pour mettre en place ou enlever un filtre
		 * pour filtrer les types de figures
		 */
		public LineFilterAction(LineType type)
		{
			this.type = type;
			String name = type.toString();
			putValue(LARGE_ICON_KEY, IconFactory.getIcon(name));
			putValue(SMALL_ICON, IconFactory.getIcon(name + "_small"));
			putValue(NAME, name);
			putValue(SHORT_DESCRIPTION, "Set/unset " + name + " filter");
		}

		/**
		 * Opérations réalisées par l'action : Ajout ou retrait d'un filtre
		 * concernant le type de trait des figures
		 * @param event l'évènement déclenchant l'action. Peut provenir d'un
		 * bouton ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent event)
		{
			AbstractButton button = (AbstractButton) event.getSource();
			boolean selected = button.getModel().isSelected();
			/*
			 * TODO Créer un new LineFilter correspondant au "type" de cette action
			 * puis l'ajouter ou bien le retirer du drawingModel suivant la
			 * sélection du bouton
			 */
		}
	}

	/**
	 * Action pour mettre en place un filtre basé sur la couleur de remplissage
	 * courante
	 */
	private class FillColorFilterAction extends AbstractAction
	{
		/**
		 * Constructeur de l'action
		 * Met en place le raccourci clavier, l'icône et la description
		 * de l'action
		 */
		public FillColorFilterAction()
		{
			putValue(NAME, "Fill Color");
			putValue(LARGE_ICON_KEY, IconFactory.getIcon("FillColor"));
			putValue(SMALL_ICON, IconFactory.getIcon("FillColor_small"));
			putValue(SHORT_DESCRIPTION, "Set/Unset Fill Color Filter");
		}

		/**
		 * Opérations réalisées par l'action : Ajout ou retrait du filtre
		 * de couleur de remplissage en fonction de la couleur de remplissage
		 * courante.
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 * ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			AbstractButton button = (AbstractButton) e.getSource();
			boolean selected = button.getModel().isSelected();

			if (selected)
			{
				/*
				 * TODO Créer un nouveau FillColorFilter avec le fillPaint courant
				 * du drawingModel et le mettre en place dans le drawingModel
				 */
			}
			else
			{
				// TODO mettre en place un FillColorFilter null dans le drawingModel
			}
		}
	}

	/**
	 * Action pour mettre en place un filtre basé sur la couleur de trait
	 * courante
	 */
	private class EdgeColorFilterAction extends AbstractAction
	{
		/**
		 * Constructeur de l'action
		 * Met en place le raccourci clavier, l'icône et la description
		 * de l'action
		 */
		public EdgeColorFilterAction()
		{
			putValue(NAME, "Edge Color");
			putValue(LARGE_ICON_KEY, IconFactory.getIcon("EdgeColor"));
			putValue(SMALL_ICON, IconFactory.getIcon("EdgeColor_small"));
			putValue(SHORT_DESCRIPTION, "Set/Unset edge color filter");
		}

		/**
		 * Opérations réalisées par l'action : Ajout ou retrait d'un filtre
		 * concernant la couleur de trait d'après la couleur de trait courante.
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 * ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			AbstractButton button = (AbstractButton) e.getSource();
			boolean selected = button.getModel().isSelected();

			if (selected)
			{
				/*
				 * TODO Créer un nouveau EdgeColorFilter avec l'edgePaint courant
				 * dudrawingModel et le mettre en place dans le drawingModel
				 */
			}
			else
			{
				// TODO mettre en place un EdgeColorFilter null dans le drawingModel
			}
		}
	}

	/**
	 * Action réalisée pour détruire les figures sélectionnées
	 * @author davidroussel
	 */
	private class DeleteAction extends AbstractAction
	{
		public DeleteAction()
		{
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, 0));
			putValue(NAME, "Delete");
			putValue(LARGE_ICON_KEY, IconFactory.getIcon("Delete"));
			putValue(SMALL_ICON, IconFactory.getIcon("Delete_small"));
			putValue(SHORT_DESCRIPTION, "Delete selected figures");
		}

		/**
		 * Opérations réalisées par l'action : Retrait des figures
		 * sélectionnées.
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 * ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			/*
			 * TODO Enregistrer l'état et destruction des figures sélectionnées
			 * du modèle
			 */
		}
	}

	/**
	 * Action réalisée pour remonter les figures sélectionnées dans la liste
	 * des figures
	 */
	private class MoveUpAction extends AbstractAction
	{
		public MoveUpAction()
		{
			putValue(ACCELERATOR_KEY,
			         KeyStroke.getKeyStroke(KeyEvent.VK_UP,
			                                Toolkit.getDefaultToolkit()
			                                    .getMenuShortcutKeyMask()));
			putValue(NAME, "Up");
			putValue(LARGE_ICON_KEY, IconFactory.getIcon("MoveUp"));
			putValue(SMALL_ICON, IconFactory.getIcon("MoveUp_small"));
			putValue(SHORT_DESCRIPTION, "Move selected figures up");
		}

		/**
		 * Opérations réalisées par l'action : Déplacement des figures
		 * sélectionnées en haut de la liste des figures.
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 * ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			/*
			 * TODO Enregistrer l'état et monter la ou les figures
			 * sélectionnées dans l'ordre des figures du drawingModel
			 */
		}
	}

	/**
	 * Action réalisée pour descendre les figures sélectionnées dans la liste
	 * des figures
	 */
	private class MoveDownAction extends AbstractAction
	{
		public MoveDownAction()
		{
			putValue(ACCELERATOR_KEY,
			         KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
			                                Toolkit.getDefaultToolkit()
			                                    .getMenuShortcutKeyMask()));
			putValue(NAME, "Down");
			putValue(LARGE_ICON_KEY, IconFactory.getIcon("MoveDown"));
			putValue(SMALL_ICON, IconFactory.getIcon("MoveDown_small"));
			putValue(SHORT_DESCRIPTION, "Move selected figures down");
		}

		/**
		 * Opérations réalisées par l'action : Déplacement des figures
		 * sélectionnées en bas de la liste des figures.
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 * ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			/*
			 * TODO Enregistrer l'état et descendre la ou les figures
			 * sélectionnées dans l'ordre des figures du drawingModel
			 */
		}
	}

	/**
	 * Action réalisée pour appliquer le style courant aux figures
	 * sélectionnées,
	 * A savoir :
	 * <ul>
	 * <li>La couleur de remplissage courante</li>
	 * <li>La couleur de trait courante</li>
	 * <li>Le type de trait courant (style et épaisseur)</li>
	 * </ul>
	 */
	private class StyleAction extends AbstractAction
	{
		public StyleAction()
		{
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, 0));
			putValue(NAME, "Style");
			putValue(LARGE_ICON_KEY, IconFactory.getIcon("Style"));
			putValue(SMALL_ICON, IconFactory.getIcon("Style_small"));
			putValue(SHORT_DESCRIPTION,
			         "Apply current style to selected figures");
		}

		/**
		 * Opérations réalisées par l'action : Application du style courant (
		 * couleur de remplissage, couleur de trait, type de trait et épaisseur
		 * du trait) aux figures sélectionnées.
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 * ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			/*
			 * TODO Endregisrer l'état courant et appliquer le
			 * 	- fillPaint du drawingModel
			 * 	- edgePaint du drawingModel
			 * 	- stroke du drawingModel
			 * à l'ensemble des figures sélectionnées
			 */
		}
	}

	/**
	 * Action pour montrer l'ensemble des figures
	 * @author davidroussel
	 */
	private class MagicDrawAction extends AbstractAction
	{
		/**
		 * Constructeur de l'action pour dessiner toutes les figures
		 * Met en place le raccourci clavier, l'icône et la description
		 * de l'action
		 */
		public MagicDrawAction()
		{
			String name = "MagicDraw";
			putValue(NAME, "MagicDraw");
			/*
			 * Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
			 * = InputEvent.CTRL_MASK on win/linux
			 * = InputEvent.META_MASK on mac os
			 */
			putValue(ACCELERATOR_KEY,
			         KeyStroke.getKeyStroke(KeyEvent.VK_M,
			                                Toolkit.getDefaultToolkit()
			                                    .getMenuShortcutKeyMask()));
			putValue(LARGE_ICON_KEY, IconFactory.getIcon(name));
			putValue(SMALL_ICON, IconFactory.getIcon(name + "_small"));
			putValue(SHORT_DESCRIPTION, "Show figures demo");
		}

		/**
		 * Opérations réalisées par l'action
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 * ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// AbstractButton button = (AbstractButton) e.getSource();
			// boolean selected = button.getModel().isSelected();

			/*
			 * TODO Dessinez un exemple de chacune de vos figures
			 * Chaque figure à un endroit différent
			 * Chaque figure avec un fillPaint, edgePaint, edgeWidth et edgeType
			 * différents
			 */

			drawingModel.update();
		}
	}

	/**
	 * Action vide pouvant vous servir de modèle pour de nouvelles actions ...
	 * @author davidroussel
	 */
	@SuppressWarnings("unused")
	private class EmptyAction extends AbstractAction // implements ...
	{
		/**
		 * Constructeur de l'action pour ....
		 * Met en place le raccourci clavier, l'icône et la description
		 * de l'action
		 */
		public EmptyAction()
		{
			String name = "XXX";
			putValue(NAME, name);
			/*
			 * Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
			 * = InputEvent.CTRL_MASK on win/linux
			 * = InputEvent.META_MASK on mac os
			 */
			putValue(ACCELERATOR_KEY,
			         KeyStroke.getKeyStroke(KeyEvent.VK_X,
			                                Toolkit.getDefaultToolkit()
			                                    .getMenuShortcutKeyMask()));
			putValue(LARGE_ICON_KEY, IconFactory.getIcon(name));
			putValue(SMALL_ICON, IconFactory.getIcon(name + "_small"));
			putValue(SHORT_DESCRIPTION, "Description de l'action");
		}

		/**
		 * Opérations réalisées par l'action
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 * ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			AbstractButton button = (AbstractButton) e.getSource();
			boolean selected = button.getModel().isSelected();

			// drawingModel.awesomeMethod(...)
		}
	}

	/**
	 * Contrôleur d'évènement permettant de modifier le type de figures à
	 * dessiner.
	 * @note dépends de #drawingModel et #infoLabel qui doivent être non
	 * null avant instanciation
	 */
	private class ShapeItemListener implements ItemListener
	{
		/**
		 * Constructeur valué du contrôleur.
		 * Initialise le type de dessin dans {@link EditorFrame#drawingModel}
		 * et crée le {@link AbstractCreationListener} correspondant.
		 * @param initialIndex l'index du type de forme sélectionné afin de
		 * mettre en place le bon creationListener dans le
		 * {@link EditorFrame#drawingPanel}.
		 */
		public ShapeItemListener(FigureType type)
		{
			// Mise en place du type de figure
			drawingModel.setFigureType(type);

			// Mise en place du type de creationListener
			AbstractCreationListener newCreationListener =
				type.getCreationListener(drawingModel,
				                         history,
				                         infoLabel);
			if (operationMode == OperationMode.CREATION)
			{
				if (creationListener != null)
				{
					drawingPanel.removeFigureListener(creationListener);
				}
				creationListener = newCreationListener;
				drawingPanel.addFigureListener(creationListener);
			}
		}

		@Override
		public void itemStateChanged(ItemEvent e)
		{
			JComboBox<?> items = (JComboBox<?>) e.getSource();
			int index = items.getSelectedIndex();
			int stateChange = e.getStateChange();
			FigureType figureType = FigureType.fromInteger(index);
			switch (stateChange)
			{
				case ItemEvent.SELECTED:
				{
					// TODO Mise en place d'un nouveau type de figure dans le drawingModel

					/*
					 * TODO Demande d'un nouveau CreationListener à figureType
					 * TODO Si le mode d'opération courant est OperationMode.CREATION
					 * alors on retire le creationListener courant du drawingPanel
					 * pour mettre en place le nouveau et on l'enregistre dans
					 * creationListener
					 */

					break;
				}
			}
		}
	}
}
