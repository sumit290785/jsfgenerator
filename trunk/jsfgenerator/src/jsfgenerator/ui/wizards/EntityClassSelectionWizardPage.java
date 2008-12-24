package jsfgenerator.ui.wizards;

import java.util.Arrays;

import jsfgenerator.ui.model.EntityDescription;

import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

@SuppressWarnings("restriction")
public class EntityClassSelectionWizardPage extends WizardPage {

	private static final String GENERATE = "generate";
	private static final String DO_NOT_GENERATE = "do not generate";
	public static final int ENTITY_PAGE = 1 << 1;
	public static final int LIST_PAGE = 1 << 2;
	
	private static final Image IMG_CLASS = JavaPluginImages.get(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CLASS);

	protected static class GenerateEditingSupport extends EditingSupport {

		public int flag;

		private ComboBoxViewerCellEditor editor;
		
		public GenerateEditingSupport(ColumnViewer viewer, int flag) {
			super(viewer);
		
			editor = new ComboBoxViewerCellEditor(((TableViewer) viewer).getTable());
			editor.setContenProvider(new ArrayContentProvider());
			editor.setLabelProvider(new LabelProvider() {

				@Override
				public String getText(Object element) {
					return ((Boolean) element) ? GENERATE : DO_NOT_GENERATE;
				}

			});

			editor.setInput(Arrays.asList(Boolean.TRUE, Boolean.FALSE));
			this.flag = flag;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		@Override
		protected Object getValue(Object element) {

			if (ENTITY_PAGE == flag) {
				return ((EntityDescription) element).isEntityPage();
			} else {
				return ((EntityDescription) element).isListPage();
			}

		}

		@Override
		protected void setValue(Object element, Object value) {
			if (value instanceof Boolean && ENTITY_PAGE == flag) {
				((EntityDescription) element).setEntityPage((Boolean) value);
			} else if (value instanceof Boolean && LIST_PAGE == flag) {
				((EntityDescription) element).setListPage((Boolean) value);
			}
			getViewer().refresh();
		}
	}

	protected static class GenerateLabelProvider extends ColumnLabelProvider {

		private int flag;

		public GenerateLabelProvider(int flag) {
			super();
			this.flag = flag;
		}

		public String getText(Object element) {
			if (ENTITY_PAGE == flag) {
				return (((EntityDescription) element).isEntityPage()) ? GENERATE : DO_NOT_GENERATE;
			} else if (LIST_PAGE == flag) {
				return (((EntityDescription) element).isListPage()) ? GENERATE : DO_NOT_GENERATE;
			}

			return null;
		}
	}

	private TableViewer viewer;

	protected EntityClassSelectionWizardPage() {
		super("EntityClassSelectionWizardPage");
		setTitle("Entity selection");
		setDescription("Please, check the items that you want to generate");
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);
		setControl(composite);

		viewer = new TableViewer(composite, SWT.BORDER);
		viewer.setContentProvider(new ArrayContentProvider());

		// class name column
		TableViewerColumn classNameColumn = new TableViewerColumn(viewer, SWT.NONE);
		classNameColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public Image getImage(Object element) {
				return IMG_CLASS;
			}

			public String getText(Object element) {
				return ((EntityDescription) element).getEntityClassName();
			}

		});
		classNameColumn.getColumn().setText("Entity name");
		classNameColumn.getColumn().setWidth(500);

		// entity page
		TableViewerColumn columnEntityPage = new TableViewerColumn(viewer, SWT.FILL);
		columnEntityPage.setLabelProvider(new GenerateLabelProvider(ENTITY_PAGE));
		columnEntityPage.getColumn().setText("Entity page");
		columnEntityPage.getColumn().setWidth(200);

		columnEntityPage.setEditingSupport(new GenerateEditingSupport(columnEntityPage.getViewer(), ENTITY_PAGE));

		// list page
		TableViewerColumn columnListPage = new TableViewerColumn(viewer, SWT.FILL);
		columnListPage.setLabelProvider(new GenerateLabelProvider(LIST_PAGE));

		columnListPage.getColumn().setText("List page");
		columnListPage.getColumn().setWidth(200);

		columnListPage.setEditingSupport(new GenerateEditingSupport(columnEntityPage.getViewer(), LIST_PAGE));

		viewer.setInput(((MVCGenerationWizard) getWizard()).getEntityDescriptions());
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}
}
