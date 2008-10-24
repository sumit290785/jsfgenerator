package jsfgenerator.inspector.entitymodel.impl;

import java.util.ArrayList;
import java.util.List;

import jsfgenerator.dummy.MyEntity;
import jsfgenerator.dummy.MyEntity2;
import jsfgenerator.inspector.entitymodel.EntityModel;
import jsfgenerator.inspector.entitymodel.IEntityModelBuilder;
import jsfgenerator.inspector.entitymodel.forms.SimpleEntityForm;
import jsfgenerator.inspector.entitymodel.pages.EntityPageModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;

public class DummyModelEngine implements IEntityModelBuilder {

	public EntityModel createEntityModel() {
		EntityModel entityModel = new EntityModel();

		entityModel.addPageModel(createEntityModelPage());

		return entityModel;
	}

	private EntityPageModel createEntityModelPage() {
		EntityPageModel pageModel = new EntityPageModel();
//		pageModel.addForm(new SimpleEntityForm(MyEntity.class));
//		pageModel.addForm(new SimpleEntityForm(MyEntity2.class));
		pageModel.setViewId("MyEntity");
		return pageModel;
	}

	public EntityModel getEntityModel(IFile entity) {
		EntityModel entityModel = new EntityModel();
		entityModel.addPageModel(createEntityModelPage());

		return entityModel;
	}

	public boolean isEntity(IFile file) {
		ICompilationUnit cu = JavaCore.createCompilationUnitFrom(file);
		List<Class<?>> classes = getEntityClasses(cu);
		
		return false;
	}

	/**
	 * instantiates the Class classes in the compilation unit
	 * 
	 * @param compilationUnit
	 * @return classes in the compilation unit
	 */
	protected List<Class<?>> getEntityClasses(ICompilationUnit compilationUnit) {
		List<Class<?>> classes = new ArrayList<Class<?>>();

		try {
			classes.add(Class.forName(compilationUnit.getElementName()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return classes;
	}

	public void addClass(Class<?> clazz) {
		// TODO Auto-generated method stub
		
	}

	public void clearModel() {
		// TODO Auto-generated method stub
		
	}

	public void addEntity(Object entity) {
		// TODO Auto-generated method stub
		
	}
}
