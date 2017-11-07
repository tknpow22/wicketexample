package tknpow22.wicketexample.page.ajax;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

import tknpow22.wicketexample.app.AppRoles;
import tknpow22.wicketexample.app.page.AppPageBase;
import tknpow22.wicketexample.app.parts.AppPageCaption;
import tknpow22.wicketexample.dao.EmployeeDao;
import tknpow22.wicketexample.dto.Employee;

/**
 * Wicket の Ajax 機能を利用したサンプル
 */
@AuthorizeInstantiation({AppRoles.All})
public class AjaxRequest1 extends AppPageBase {

	@Override
	protected void onInitialize() {
		super.onInitialize();

		wicketAjax = true;

		add(new AppPageCaption("page-caption", "Wicket の Ajax 機能を利用したサンプル"));

		add(new AjaxForm("ajaxForm"));
	}

	private class AjaxForm extends Form<Void> {
		private String employeeName = "山田";
		@SuppressWarnings("unused")
		private String searchWord = "";
		private List<Employee> employeeInfo = new ArrayList<>();

		public AjaxForm(String id) {
			super(id);
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();

			setDefaultModel(new CompoundPropertyModel<>(this));

			add(new TextField<>("employeeName"));

			// searchWord
			Component searchWordComponent = new Label("searchWord").setOutputMarkupId(true);
			add(searchWordComponent);

			// employeeList
			WebMarkupContainer employeeListComponent = new WebMarkupContainer("employeeList");
			employeeListComponent.setOutputMarkupId(true);
			{
				ListView<Employee> employeeInfoComponent = new ListView<Employee>("employeeInfo", employeeInfo) {
					@Override
					protected void populateItem(ListItem<Employee> item) {
						Employee ei = item.getModelObject();
						item.add(new Label("employeeId", ei.getEmployeeId()));
						item.add(new Label("employeeName", ei.getEmployeeName()));
						item.add(new Label("age", ei.getAge()));
					}
				};
				employeeListComponent.add(employeeInfoComponent);
			}
			add(employeeListComponent);

			add(new AjaxButton("search") {

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					if (target != null) {

						searchWord = employeeName;

						try (EmployeeDao dao = new EmployeeDao()) {
							List<Employee> employees = dao.findEmployeesByName(employeeName);

							employeeInfo.clear();
							employeeInfo.addAll(employees);
						}

						target.add(searchWordComponent);
						target.add(employeeListComponent);
					}
				}
			});
		}
	}
}
