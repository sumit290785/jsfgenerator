package jsfgenerator.ui.wizards;

import jsfgenerator.ui.model.ProjectResourceProvider;
import jsfgenerator.ui.providers.ResourceLabelProvider;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.ui.actions.OpenNewPackageWizardAction;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ControllerTargetPackageSelectionWizardPage extends WizardPage {
	
	private IPackageFragment selectedPackageFragment;

	private TableViewer packageViewer;

	protected ControllerTargetPackageSelectionWizardPage() {
		super("ControllerTargetPackageSelectionWizardPage");
		setTitle("Controller target package selection");
		setDescription("Please, select a target package for the generated controller java classes");
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);

		composite.setLayout(new GridLayout(1, false));

		final Button newPackageButton = new Button(composite, SWT.PUSH);
		newPackageButton.setText("create package");

		newPackageButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
				OpenNewPackageWizardAction action = new OpenNewPackageWizardAction();
				action.setSelection(new StructuredSelection(ProjectResourceProvider.getInstance().getJsfProject()));
				action.run();

				if (packageViewer != null) {
					packageViewer.setInput(ProjectResourceProvider.getInstance().getJsfProjectPackageFragments());
					validate();
				}

			}
		});

		packageViewer = new TableViewer(composite, SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
		packageViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		packageViewer.setLabelProvider(new ResourceLabelProvider());
		packageViewer.setContentProvider(new ArrayContentProvider());

		packageViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		packageViewer.setInput(ProjectResourceProvider.getInstance().getJsfProjectPackageFragments());

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
