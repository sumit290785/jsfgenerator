package jsfgenerator.inspector.entitymodel;


/**
 * MVC design pattern's model element can be defined in many different ways! A
 * set of classes, an uml class diagram, an ecore model defined by eclipse
 * community, etc.
 * 
 * JSF generator uses the model (in MVC) to generate the view (MVC) xhtml files,
 * and also to generate the controller (MVC) java classes! To do this it uses an
 * EntityModel object! EntityModel is a model (in MVC) independent
 * representation model of the entity model! In other words. EntityModel is a
 * metamodel of the application model!
 * 
 * To handle different models an interface is required! This IEntityModelEngine
 * interface is to build this bridge between the generator and real application model!
 * 
 * @author zoltan verebes
 * 
 */
public interface IEntityModelEngine {

	/**
	 * 
	 * @return metamodel of the application model
	 */
	public EntityModel getEntityModel();

}
