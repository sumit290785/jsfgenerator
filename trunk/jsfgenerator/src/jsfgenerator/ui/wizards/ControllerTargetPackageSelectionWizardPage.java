package jsfgenerator.ui.wizards;

import jsfgenerator.ui.providers.ResourceLabelProvider;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class ControllerTargetPackageSelectionWizardPage extends WizardPage {

	protected ControllerTargetPackageSelectionWizardPage() {
		super("ControllerTargetPackageSelectionWizardPage");
		setTitle("Controller target package selection");
		setDescription("Please, select a target package for the generated controller java classes");
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);

		final GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);

		final ListViewer packageViewer = new ListViewer(composite, SWT.BORDER);
		packageViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		packageViewer.setLabelProvider(new ResourceLabelProvider());
		packageViewer.setContentProvider(new ArrayContentProvider());
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		// TODO
		IJavaProject project = JavaCore.create(root.getProject("proba"));
		
		try {
			project.findType("pkg.aaa");
			//IPackageFragmentRoot packageFragmentRoot = project.getPackageFragmentRoot(project.getResource()).getParent();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
