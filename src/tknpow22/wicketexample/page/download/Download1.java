package tknpow22.wicketexample.page.download;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;

import tknpow22.wicketexample.app.AppRoles;
import tknpow22.wicketexample.app.page.AppPageBase;
import tknpow22.wicketexample.app.parts.AppFeedbackPanel;
import tknpow22.wicketexample.app.parts.AppPageCaption;
import tknpow22.wicketexample.dao.EmployeeDao;
import tknpow22.wicketexample.dto.Employee;

/**
 * ダウンロード処理のサンプル
 */
@AuthorizeInstantiation({AppRoles.All})
public class Download1 extends AppPageBase {

	@Override
	protected void onInitialize() {
		super.onInitialize();

		add(new AppPageCaption("page-caption", "ダウンロード１"));

		add(new DownloadForm("downloadForm"));
	}

	/**
	 * ダウンロードフォームと処理を定義する。
	 */
	private class DownloadForm extends Form<Void> {

		private Employee employee = null;

		public DownloadForm(String id) {
			super(id);
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();

			setDefaultModel(new CompoundPropertyModel<>(this));

			// employee
			try (EmployeeDao dao = new EmployeeDao()) {
				List<Employee> employees = dao.findAllEmployees();

				if (0 < employees.size()) {
					employee = employees.get(0);
				}

				DropDownChoice<Employee> employeeComponent = new DropDownChoice<Employee>("employee", employees,
						new ChoiceRenderer<Employee>("employeeName", "employeeId"));

				employeeComponent.setNullValid(true);
				// NOTE: setNullValid(true|false) 設定時の動作についてのメモ
				//if (employeeComponent.isNullValid() == false) {
				//	// 空項目の値: Download1.properties の employee.null、存在しなければ Wicket のデフォルトリソースから取得される
				//	if (employees.size() == 0 || employee == null) {
				//		// select の先頭に空項目が追加される
				//	} else {
				//		// select の先頭に空項目は追加されない
				//	}
				//	// NOTE: select で空項目以外を選択し submit すると、select から空項目は無くなってしまうので注意
				//} else /*if (employeeComponent.isNullValid() == true)*/ {
				//	// 空項目の値: Download1.properties の employee.nullValid、存在しなければ Wicket のデフォルトリソースから取得される
				//
				//	// 常に空項目が追加される
				//}
				add(employeeComponent);
			}

			add(new AppFeedbackPanel("feedbackPanel"));
		}


		@Override
		protected void onError() {
		}

		@Override
		protected void onSubmit() {

			if (employee == null) {
				error("従業員名を選んでください。");
			} else {

				try (EmployeeDao dao = new EmployeeDao()) {
					Employee foundEmployee = dao.findEmployeeById(employee.getEmployeeId());
					if (foundEmployee != null) {

						getRequestCycle().scheduleRequestHandlerAfterCurrent(
						new ResourceStreamRequestHandler(
							new AbstractResourceStreamWriter() {

								@Override
								public String getContentType() {
									return "application/octet-stream";
								}

								@Override
								public void write(OutputStream output) throws IOException {
									String outputString = String.format("\"%s\",\"%s\",\"%d\"",
											foundEmployee.getEmployeeId(),
											foundEmployee.getEmployeeName(),
											foundEmployee.getAge()
										);

									output.write(outputString.getBytes("Shift_JIS"));
									output.flush();
									output.close();
								}

							},
							String.format("%s.csv", foundEmployee.getEmployeeId())
						));

					} else {
						error("従業員が存在しません。");
					}
				}
			}
		}
	}
}
