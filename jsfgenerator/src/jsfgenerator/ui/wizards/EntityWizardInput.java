package jsfgenerator.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Type;

/**
 * input element of the wizard
 * 
 * @author zoltan verebes
 * 
 */
public class EntityWizardInput {

	public static class EntityFieldInput {
		
		private EntityWizardInput parent;

		private String fieldName;

		private Type fieldType;
		
		public EntityFieldInput(String fieldName, Type fieldType, EntityWizardInput parent) {
			this.fieldName = fieldName;
			this.fieldType = fieldType;
			this.parent = parent;
		}

		public void setParent(EntityWizardInput parent) {
			this.parent = parent;
		}

		public EntityWizardInput getParent() {
			return parent;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldType(Type fieldType) {
			this.fieldType = fieldType;
		}

		public Type getFieldType() {
			return fieldType;
		}
	}

	private String name;

	private List<EntityFieldInput> fields = new ArrayList<EntityFieldInput>();

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addField(String name, Type type) {
		fields.add(new EntityFieldInput(name, type, this));
	}

	public List<EntityFieldInput> getFields() {
		return fields;
	}

}
