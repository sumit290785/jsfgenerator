package jsfgenerator.ui.composites;

import java.util.Arrays;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

public class ResourceSelectionComposite extends Composite {

	protected ILabelProvider labelProvider;
	protected ITreeContentProvider contentProvider;
	protected ViewerFilter viewerFilter;
	
	protected FilteredTree filteredTree;

	protected Text selectionText;

	public ResourceSelectionComposite(Composite parent, int style, ILabelProvider labelProvider,
			ITreeContentProvider contentProvider, ViewerFilter viewerFilter) {
		super(parent, style);
		this.labelProvider = labelProvider;
		this.contentProvider = contentProvider;
		this.viewerFilter = viewerFilter;
		createComposite();
	}

	private void createComposite() {
		GridLayout layout = new GridLayout(2, false);
		setLayout(layout);

		filteredTree = new FilteredTree(this, SWT.BORDER, new PatternFilter());
		filteredTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		TreeViewer viewer = filteredTree.getViewer();
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(labelProvider);
		
		viewer.setComparator(new ViewerComparator() {

			@Override
			public int category(Object element) {
				if (element instanceof IFolder) {
					return 1;
				}
				return 0;
			}

		});

		viewer.setFilters(Arrays.asList(viewerFilter).toArray(new ViewerFilter[0]));

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		filteredTree.getViewer().setInput(root);

		final Label label = new Label(this, SWT.NONE);
		label.setText("Selected: ");
		selectionText = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		selectionText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	}
	
	public FilteredTree getFilteredTree() {
		return filteredTree;
	}
	
	public Text getSelectionText() {
		return selectionText;
	}

}
