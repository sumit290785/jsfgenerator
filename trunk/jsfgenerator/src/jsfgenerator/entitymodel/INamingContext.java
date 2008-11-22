package jsfgenerator.entitymodel;

/**
 * provided names are used as reference name of the generared tags by the tag
 * tree provider.
 * 
 * Its implementors are such classes as PageModel, EntityField or EntityForm
 * 
 * @author zoltan verebes
 * 
 */
public interface INamingContext {

	public String getName();

}
