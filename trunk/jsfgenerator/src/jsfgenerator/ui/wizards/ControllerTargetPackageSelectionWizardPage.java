package jsfgenerator.ui.wizards;

import jsfgenerator.ui.model.ProjectResourceProvider;
import jsfgenerator.ui.providers.ResourceLabelProvider;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class ControllerTargetPackageSelectionWizardPage extends WizardPage {

	private IPackageFragment selectedPackageFragment;

	protected ControllerTargetPackageSelectionWizardPage() {
		super("ControllerTargetPackageSelectionWizardPage");
		setTitle("Controller target package selection");
		setDescription("Please, select a target package for the generated controller java classes");
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);

		composite.setLayout(new GridLayout(1, false));

		final TableViewer packageViewer = new TableViewer(composite, SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
		packageViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		packageViewer.setLabelProvider(new ResourceLabelProvider());
		packageViewer.setContentProvider(new ArrayContentProvider());

		packageViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		packageViewer.setInput(ProjectResourceProvider.getInstance().getProjectPackageFragments());

		packageViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection() != null && event.getSelection() instanceof StructuredSelection) {
					selectedPackageFragment = (IPackageFragment) ((StructuredSelection) event.getSelection()).getFirstElement();
				}

				validate();
			}
		});

		validate();
	}

	public IPackageFragment getSelectedPackageFragment() {
		return selectedPackageFragment;
	}

	private void validate() {
		setErrorMessage(selectedPackageFragment == null ? "Please, select a target package" : null);
		setPageComplete(getErrorMessage() == null);
	}

}
