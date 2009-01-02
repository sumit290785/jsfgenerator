package jsfgenerator.generation.common.treebuilders;

import jsfgenerator.entitymodel.pageelements.ColumnModel;
import jsfgenerator.entitymodel.pageelements.EntityRelationship;
import jsfgenerator.entitymodel.pageelements.ReferencedColumnModel;
import jsfgenerator.entitymodel.pages.EntityListPageModel;
import jsfgenerator.generation.common.utilities.QueryBuilder;
import jsfgenerator.generation.common.visitors.ReferenceNameEvaluatorVisitor;
import jsfgenerator.generation.controller.AbstractControllerNodeFactory;
import jsfgenerator.generation.controller.ControllerTree;
import jsfgenerator.generation.controller.nodes.ClassControllerNode;
import jsfgenerator.generation.controller.nodes.FunctionControllerNode;
import jsfgenerator.generation.view.IViewTemplateProvider;
import jsfgenerator.generation.view.PlaceholderTagNode;
import jsfgenerator.generation.view.ViewTemplateTree;
import jsfgenerator.generation.view.PlaceholderTagNode.PlaceholderTagNodeType;

public class EntityListPageTreeBuilder extends AbstractTreeBuilder {

	private EntityListPageModel model;

	private ViewTemplateTree templateTree;

	private PlaceholderTagNode columnDataPlaceholderNode;

	private PlaceholderTagNode columnHeaderPlaceholderNode;

	private ClassControllerNode classNode;

	private String varVariableName;
	/*
	 * it is used to keep the information about the class, its fields and functions which is created as backing bean
	 */
	private ControllerTree controllerTree;

	public EntityListPageTreeBuilder(EntityListPageModel model, IViewTemplateProvider tagTreeProvider,
			AbstractControllerNodeFactory controllerNodeProvider) {
		super(tagTreeProvider, controllerNodeProvider);
		this.model = model;
		init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.generation.common.treebuilders.AbstractTreeBuilder#getControllerTree()
	 */
	@Override
	public ControllerTree getControllerTree() {
		return controllerTree;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsfgenerator.generation.common.treebuilders.AbstractTreeBuilder#getViewTemplateTree()
	 */
	@Override
	public ViewTemplateTree getViewTemplateTree() {
		return templateTree;
	}

	protected void init() {
		this.templateTree = templateTreeProvider.getEntityListPageTemplateTree();
		columnDataPlaceholderNode = getFirstPlaceholderTagNodeByType(templateTree, PlaceholderTagNodeType.LIST_COLUMN_DATA);

		if (columnDataPlaceholderNode == null) {
			throw new RuntimeException("Placeholder for the columns not found! Please revisit the template definition");
		}

		columnHeaderPlaceholderNode = getFirstPlaceholderTagNodeByType(templateTree, PlaceholderTagNodeType.LIST_COLUMN_HEADER);

		varVariableName = getVarVariableName(templateTree);

		if (varVariableName == null) {
			throw new RuntimeException("Variable of the repeater not found! Please revisit the template definition");
		}

		ReferenceNameEvaluatorVisitor visitor = new ReferenceNameEvaluatorVisitor(model.getViewId(), model.getEntityClassName());
		templateTree.apply(visitor);

		/*
		 * add the root CLASS to the controller tree it will keep all of its elements as children in the tree
		 */
		this.controllerTree = new ControllerTree();
		classNode = controllerNodeProvider.createEntityListPageClassNode(model);
		controllerTree.addNode(classNode);
		
		QueryBuilder.getInstance().clear();
		QueryBuilder.getInstance().setDomainEntityClass(model.getEntityClassName());
	}

	public void addColumn(ColumnModel column) {

		String entityName;
		if (column instanceof ReferencedColumnModel) {
			addReferencedColumnModel((ReferencedColumnModel) column);
			entityName = ((ReferencedColumnModel) column).getReferencedEntityClassName();
			;
		} else {
			addColumnModel(column);
			entityName = column.getEntityClassName();
		}

		if (columnHeaderPlaceholderNode != null) {
			ViewTemplateTree columnHeaderTree = templateTreeProvider.getListColumnHeaderTemplateTree();
			columnHeaderTree.applyReferenceName(column.getFieldName());

			ReferenceNameEvaluatorVisitor visitor = new ReferenceNameEvaluatorVisitor(model.getViewId(), entityName);
			visitor.setEntityFieldName(column.getFieldName());
			columnHeaderTree.apply(visitor);

			columnHeaderPlaceholderNode.addAllChildren(columnHeaderTree.getNodes());
		}
	}

	private void addColumnModel(ColumnModel column) {
		ViewTemplateTree columnDataTree = templateTreeProvider.getListColumnDataTemplateTree();
		columnDataTree.applyReferenceName(column.getFieldName());

		ReferenceNameEvaluatorVisitor visitor = new ReferenceNameEvaluatorVisitor(model.getViewId(), column.getEntityClassName());
		visitor.setEntityFieldName(column.getFieldName());
		visitor.setVarVariable(varVariableName);
		columnDataTree.apply(visitor);
		columnDataPlaceholderNode.addAllChildren(columnDataTree.getNodes());
	}

	private void addReferencedColumnModel(ReferencedColumnModel column) {
		if (column.getRelationshipToDomainEntity().equals(EntityRelationship.ONE_TO_MANY)
				|| column.getRelationshipToDomainEntity().equals(EntityRelationship.MANY_TO_MANY)) {
			addCollectionReferencedColumnModel(column);
			QueryBuilder.getInstance().addCollectionReference(column.getReferencedFieldName());
		} else {
			addSimpleReferencedColumnModel(column);
			QueryBuilder.getInstance().addSingleReference(column.getReferencedFieldName());
		}
	}

	private void addCollectionReferencedColumnModel(ReferencedColumnModel column) {
		ViewTemplateTree collectionColumnTree = templateTreeProvider.getListCollectionColumnTemplateTree();

		PlaceholderTagNode placeholder = getFirstPlaceholderTagNodeByType(collectionColumnTree,
				PlaceholderTagNodeType.LIST_COLLECTION_COLUMN_DATA);
		if (placeholder == null) {
			throw new RuntimeException("List collection column data placeholder not found");
		}

		ReferenceNameEvaluatorVisitor visitor = new ReferenceNameEvaluatorVisitor(model.getViewId(), column.getEntityClassName());
		visitor.setEntityFieldName(column.getFieldName());
		visitor.setVarVariable(varVariableName);
		collectionColumnTree.apply(visitor);

		ViewTemplateTree collectionColumnDataTree = templateTreeProvider.getListCollectionColumnDataTemplateTree();
		visitor = new ReferenceNameEvaluatorVisitor(model.getViewId(), column.getReferencedEntityClassName());
		visitor.setEntityFieldName(column.getReferencedFieldName());
		visitor.setVarVariable(getVarVariableName(collectionColumnTree));
		collectionColumnDataTree.apply(visitor);

		placeholder.addAllChildren(collectionColumnDataTree.getNodes());

		columnDataPlaceholderNode.addAllChildren(collectionColumnTree.getNodes());
	}

	private void addSimpleReferencedColumnModel(ReferencedColumnModel column) {
		ViewTemplateTree columnDataTree = templateTreeProvider.getListColumnDataTemplateTree();
		columnDataTree.applyReferenceName(column.getFieldName());

		ReferenceNameEvaluatorVisitor visitor = new ReferenceNameEvaluatorVisitor(varVariableName + "." + column.getFieldName()
				+ "." + column.getReferencedFieldName(), column.getReferencedEntityClassName());
		visitor.setEntityFieldName(column.getReferencedFieldName());
		columnDataTree.apply(visitor);

		columnDataPlaceholderNode.addAllChildren(columnDataTree.getNodes());
	}

	public void buildQuery() {
		if (classNode != null) {
			FunctionControllerNode node = controllerNodeProvider.createListQueryFunctionNode(QueryBuilder.getInstance()
					.getQueryString());
			classNode.addChild(node);
		}
	}
}
