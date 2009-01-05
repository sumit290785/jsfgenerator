package jsfgenerator.ui.validation;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Presents validation result of the view template validation
 * 
 * @author zoltan verebes
 * 
 */
public class ValidationResultDialog extends Dialog {

	private List<String> messages;

	public ValidationResultDialog(Shell parentShell, List<String> messages) {
		super(parentShell);
		this.messages = messages;
	}

	@Override
	protected Control createContents(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setSize(500, 600);

		composite.setLayout(new GridLayout(1, true));
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Following errors were found by the validation");

		final ListViewer viewer = new ListViewer(composite);
		viewer.getList().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new ArrayContentProvider());

		viewer.setLabelProvider(new LabelProvider() {

			public Image getImage(Object arg0) {
				return null;
			}

			public String getText(Object msg) {
				if (msg instanceof String) {
					return (String) msg;
				}

				return null;
			}

		});

		viewer.setInput(messages.size() > 0 ? messages : Arrays.asList("Validation passed"));

		final Button ok = new Button(composite, SWT.PUSH);
		ok.setText("OK");
		GridData data = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ValidationResultDialog.this.close();
			}
		});

		return composite;
	}

}
