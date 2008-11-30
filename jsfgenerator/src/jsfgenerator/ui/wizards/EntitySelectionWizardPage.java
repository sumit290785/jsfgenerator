package jsfgenerator.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import jsfgenerator.ui.providers.EntitySelectionContentProvider;
import jsfgenerator.ui.providers.EntitySelectionLabelProvider;
import jsfgenerator.ui.wizards.EntityWizardInput.EntityFieldInput;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class EntitySelectionWizardPage extends WizardPage {

	private List<EntityWizardInput> entities;

	private CheckboxTreeViewer treeViewer;
	
	protected EntitySelectionWizardPage(List<EntityWizardInput> entities) {
		super("SingleEntityWizardPage");
		setTitle("Single entity page");
		setDescription("This is a single wizard page");
		this.entities = entities;
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);
		setControl(composite);

		treeViewer = new CheckboxTreeViewer(composite);
		treeViewer.setContentProvider(new EntitySelectionContentProvider());
		treeViewer.setLabelProvider(new EntitySelectionLabelProvider());
		treeViewer.setInput(entities);
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.expandAll();
		treeViewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked() && event.getElement() instanceof EntityFieldInput) {
					EntityFieldInput field = (EntityFieldInput) event.getElement();
					treeViewer.setGrayChecked(field.getParent(), true);
					return;
				}

				if (event.getElement() instanceof EntityWizardInput) {
					treeViewer.setSubtreeChecked(event.getElement(), event.getChecked());
				}
			}

		});
		
		for (EntityWizardInput input : entities) {
			treeViewer.setSubtreeChecked(input, true);
		}
	}

	public List<EntityWizardInput> getSelectedEntities() {
		List<EntityWizardInput> selectedElements = new ArrayList<EntityWizardInput>();
		for (Object obj : treeViewer.getCheckedElements()) {
			if (obj instanceof EntityWizardInput) {
				EntityWizardInput input = new EntityWizardInput();
				input.setName(((EntityWizardInput)obj).getName());

				for (Object element : treeViewer.getCheckedElements()) {
					if (element instanceof EntityFieldInput && ((EntityFieldInput) element).getParent().equals(obj)) {
						EntityFieldInput fieldInput = (EntityFieldInput) element;
						input.addField(fieldInput.getFieldName(), fieldInput.getFieldType());
					}
				}

				selectedElements.add(input);
			}
		}
		return selectedElements;
	}

}
