package jsfgenerator.ui.wizards.wizardpages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jsfgenerator.ui.model.EntityDescriptionEntityPageWrapper;
import jsfgenerator.ui.model.EntityDescriptionListPageWrapper;
import jsfgenerator.ui.wizards.MVCGenerationWizard;

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

	public static class WizardInput {
		private EntityDescriptionEntityPageWrapper entityPageWrapper;
		private EntityDescriptionListPageWrapper listPageWrapper;

		public WizardInput(EntityDescriptionEntityPageWrapper entityPageWrapper, EntityDescriptionListPageWrapper listPageWrapper) {
			this.entityPageWrapper = entityPageWrapper;
			this.listPageWrapper = listPageWrapper;
		}

		public EntityDescriptionEntityPageWrapper getEntityPageWrapper() {
			return entityPageWrapper;
		}

		public EntityDescriptionListPageWrapper getListPageWrapper() {
			return listPageWrapper;
		}

	}

	private static final String GENERATE = "generate";
	private static final String DO_NOT_GENERATE = "do not generate";

	private static final Image IMG_CLASS = JavaPluginImages.get(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CLASS);

	protected static class GenerateEditingSupport extends EditingSupport {

		private ComboBoxViewerCellEditor editor;

		public static final int LIST_PAGE = 1 << 1;
		public static final int ENTITY_PAGE = 1 << 2;
		private int flag;

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
			if ((flag & ENTITY_PAGE) == ENTITY_PAGE) {
				return ((WizardInput) element).getEntityPageWrapper().isPageGenerated();
			} else if ((flag & LIST_PAGE) == LIST_PAGE) {
				return ((WizardInput) element).getListPageWrapper().isPageGenerated();
			}

			return null;
		}

		@Override
		protected void setValue(Object element, Object value) {
			if (value instanceof Boolean && (flag & ENTITY_PAGE) == ENTITY_PAGE) {
				((WizardInput) element).getEntityPageWrapper().setPageGenerated((Boolean) value);
			} else if (value instanceof Boolean && (flag & LIST_PAGE) == LIST_PAGE) {
				((WizardInput) element).getListPageWrapper().setPageGenerated((Boolean) value);
			}
			getViewer().refresh();
		}
	}

	private TableViewer viewer;

	public EntityClassSelectionWizardPage() {
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
				return ((WizardInput) element).getEntityPageWrapper().getEntityDescription().getEntityClassName();
			}

		});
		classNameColumn.getColumn().setText("Entity name");
		classNameColumn.getColumn().setWidth(500);

		// entity page
		TableViewerColumn columnEntityPage = new TableViewerColumn(viewer, SWT.FILL);
		columnEntityPage.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((WizardInput) element).getEntityPageWrapper().isPageGenerated() ? GENERATE : DO_NOT_GENERATE;
			}
		});
		columnEntityPage.getColumn().setText("Entity page");
		columnEntityPage.getColumn().setWidth(200);

		columnEntityPage.setEditingSupport(new GenerateEditingSupport(columnEntityPage.getViewer(),
				GenerateEditingSupport.ENTITY_PAGE));

		// list page
		TableViewerColumn columnListPage = new TableViewerColumn(viewer, SWT.FILL);
		columnListPage.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((WizardInput) element).getListPageWrapper().isPageGenerated() ? GENERATE : DO_NOT_GENERATE;
			}
		});

		columnListPage.getColumn().setText("List page");
		columnListPage.getColumn().setWidth(200);

		columnListPage.setEditingSupport(new GenerateEditingSupport(columnEntityPage.getViewer(),
				GenerateEditingSupport.LIST_PAGE));

		MVCGenerationWizard wizard = (MVCGenerationWizard) getWizard();

		Iterator<EntityDescriptionEntityPageWrapper> itEntity = wizard.getEntityDescriptionEntityPageWrappers().iterator();
		Iterator<EntityDescriptionListPageWrapper> itList = wizard.getEntityDescriptionListPageWrappers().iterator();
		List<WizardInput> input = new ArrayList<WizardInput>();
		while (itEntity.hasNext() && itList.hasNext()) {
			input.add(new WizardInput(itEntity.next(), itList.next()));
		}

		viewer.setInput(input);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}
}
