package io.onedev.server.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.unbescape.java.JavaEscape;

import com.google.common.collect.Lists;

import io.onedev.commons.utils.StringUtils;
import io.onedev.server.OneDev;
import io.onedev.server.entitymanager.SettingManager;
import io.onedev.server.model.Project;
import io.onedev.server.model.support.administration.GlobalIssueSetting;
import io.onedev.server.model.support.issue.field.spec.FieldSpec;
import io.onedev.server.security.SecurityUtils;
import io.onedev.server.util.validation.ProjectPathValidator;
import io.onedev.server.web.editable.BeanDescriptor;
import io.onedev.server.web.editable.PropertyDescriptor;

public class IssueUtils {
	
	private static final List<String> ISSUE_FIX_WORDS = Lists.newArrayList(
			"fix", "fixed", "fixes", "fixing", 
			"resolve", "resolved", "resolves", "resolving", 
			"close", "closed", "closes", "closing");
	
    private static final Pattern ISSUE_FIX_PATTERN;
    
    static {
    	StringBuilder builder = new StringBuilder("(^|[\\W|/]+)(");
    	builder.append(StringUtils.join(ISSUE_FIX_WORDS, "|"));
    	builder.append(")\\s+issue\\s+(");
    	builder.append(JavaEscape.unescapeJava(ProjectPathValidator.PATTERN.pattern()));
    	builder.append(")?#(\\d+)(?=$|[\\W|/]+)");
    	ISSUE_FIX_PATTERN = Pattern.compile(builder.toString());
    }
    
	private static final String FIELD_BEAN_CLASS = "IssueFieldBean";
	
	public static void clearFields(Serializable fieldBean) {
		for (List<PropertyDescriptor> groupProperties: new BeanDescriptor(fieldBean.getClass()).getProperties().values()) {
			for (PropertyDescriptor property: groupProperties) 
				property.setPropertyValue(fieldBean, null);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Class<? extends Serializable> defineFieldBeanClass() {
		GlobalIssueSetting issueSetting = OneDev.getInstance(SettingManager.class).getIssueSetting();
		return (Class<? extends Serializable>) FieldSpec.defineClass(FIELD_BEAN_CLASS, 
				"Issue Fields", issueSetting.getFieldSpecs());
	}
	
	@Nullable
	public static Class<? extends Serializable> loadFieldBeanClass(String className) {
		if (className.equals(FIELD_BEAN_CLASS)) 
			return defineFieldBeanClass();
		else 
			return null;
	}
	
	public static Collection<String> getPropertyNames(Project project, Class<?> fieldBeanClass, Collection<String> fieldNames) {
		Collection<String> propertyNames = new HashSet<>();
		SettingManager settingManager = OneDev.getInstance(SettingManager.class); 
		for (List<PropertyDescriptor> groupProperties: new BeanDescriptor(fieldBeanClass).getProperties().values()) {
			for (PropertyDescriptor property: groupProperties) {
				if (fieldNames.contains(property.getDisplayName())) {
					FieldSpec field = settingManager.getIssueSetting().getFieldSpec(property.getDisplayName());
					if (field != null && SecurityUtils.canEditIssueField(project, field.getName()))
						propertyNames.add(property.getPropertyName());
				}
			}
		}
		return propertyNames;
	}
	
	public static Map<String, Object> getFieldValues(ComponentContext context, Serializable fieldBean, Collection<String> fieldNames) {
		ComponentContext.push(context);
		try {
			Map<String, Object> fieldValues = new HashMap<>();
			BeanDescriptor beanDescriptor = new BeanDescriptor(fieldBean.getClass());
			for (List<PropertyDescriptor> groupProperties: beanDescriptor.getProperties().values()) {
				for (PropertyDescriptor property: groupProperties) {
					if (fieldNames.contains(property.getDisplayName()))
						fieldValues.put(property.getDisplayName(), property.getPropertyValue(fieldBean));
				}
			}
			
			return fieldValues;
		} finally {
			ComponentContext.pop();
		}
	}
	
	public static Collection<Long> parseFixedIssueNumbers(Project project, String commitMessage) {
		Collection<Long> issueNumbers = new HashSet<>();

		// Skip unmatched commit message quickly 
		boolean fixWordsFound = false;
		String lowerCaseCommitMessage = commitMessage.toLowerCase();
		for (String word: ISSUE_FIX_WORDS) {
			if (lowerCaseCommitMessage.indexOf(word) != -1) {
				fixWordsFound = true;
				break;
			}
		}
		
		if (fixWordsFound 
				&& lowerCaseCommitMessage.contains("#") 
				&& lowerCaseCommitMessage.contains("issue")) {
			Matcher matcher = ISSUE_FIX_PATTERN.matcher(lowerCaseCommitMessage);
			
			while (matcher.find()) {
				String projectPath = matcher.group(3);
				if (projectPath == null || projectPath.equals(project.getPath()))
					issueNumbers.add(Long.parseLong(matcher.group(matcher.groupCount())));
			}
		}
		
		return issueNumbers;
	}
	
}
