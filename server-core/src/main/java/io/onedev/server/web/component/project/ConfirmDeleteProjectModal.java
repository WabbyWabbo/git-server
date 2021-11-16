package io.onedev.server.web.component.project;

import org.apache.wicket.ajax.AjaxRequestTarget;

import io.onedev.server.OneDev;
import io.onedev.server.entitymanager.ProjectManager;
import io.onedev.server.model.Project;
import io.onedev.server.web.component.modal.confirm.ConfirmModalPanel;

@SuppressWarnings("serial")
public abstract class ConfirmDeleteProjectModal extends ConfirmModalPanel {

	public ConfirmDeleteProjectModal(AjaxRequestTarget target) {
		super(target);
	}

	@Override
	protected void onConfirm(AjaxRequestTarget target) {
		Project project = getProject();
		
		OneDev.getInstance(ProjectManager.class).delete(project);
		getSession().success("项目 '" + project.getPath() + "' 已删除");
		
		onDeleted(target);
	}

	protected abstract void onDeleted(AjaxRequestTarget target);
	
	@Override
	protected String getConfirmMessage() {
		return "此项目及其子项目将被删除且无法恢复， "
				+ "请输入下面的项目路径 <code>" + getProject().getPath() + "</code> 来确认删除。";
	}

	@Override
	protected String getConfirmInput() {
		return getProject().getPath();
	}

	protected abstract Project getProject();
	
}
