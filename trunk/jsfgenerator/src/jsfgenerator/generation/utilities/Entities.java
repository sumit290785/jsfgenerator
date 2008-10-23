package jsfgenerator.generation.utilities;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Entities {
	
	protected static boolean hasGetter(Class<?> clazz, String fieldName) {
		return true;
	}
	
	protected static boolean hasSetter(Class<?> clazz, String fieldName) {
		return true;
	}
	
	public static List<EntityField> getEntityFields(Class<?> entityClass) {
		List<EntityField> entityFields = new ArrayList<EntityField>();
		for (Field field: entityClass.getDeclaredFields()) {
			String name = field.getName();
			if (hasGetter(entityClass, name) && hasSetter(entityClass, name)) {
				entityFields.add(new EntityField(name, field.getType()));
			}
		}
		
		return entityFields;
	}

}
