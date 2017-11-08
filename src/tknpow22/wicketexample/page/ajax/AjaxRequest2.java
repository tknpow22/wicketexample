package tknpow22.wicketexample.page.ajax;

import java.util.List;
import java.util.Map;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.TextRequestHandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import tknpow22.wicketexample.app.AppRoles;
import tknpow22.wicketexample.app.page.AppPageBase;
import tknpow22.wicketexample.app.parts.AjaxHiddenPanel;
import tknpow22.wicketexample.app.parts.AppPageCaption;
import tknpow22.wicketexample.app.util.AppUtils;
import tknpow22.wicketexample.dao.EmployeeDao;
import tknpow22.wicketexample.dto.Employee;

/**
 * 素の jQuery ajax を利用したサンプル
 *
 * ページ表示後、Chrome でページのソースを表示した場合、ページ取得のリクエストが送られ
 * ページのバージョン番号が変わるため、ブラウザ側で保持している callbackUrl が古くなって、
 * その後の ajax リクエストは失敗する。
 * その際は、ページ全体を F5 などでリフレッシュしてから、やり直す必要がある。
 *
 * ちなみに Wicket の Ajax 機能では、バージョン番号変更を検知して、GET リクエストを送り、
 * ページを自動的にリロードしている(その代わり、入力した内容は前の状態に戻ってしまう)。
 *
 * * Chrome でソース表示時にリクエスト送ってるのは知らなかった…ちなみに、IE は送ってないようだ。
 * * 手元の Chrome は バージョン: 62.0.3202.89、IE は バージョン: 11.674.15063.0。
 * * 今後動作が変わるかもなので、以上、余談です。
 */
@AuthorizeInstantiation({AppRoles.All})
public class AjaxRequest2 extends AppPageBase {

	//// with Jackson: static class
	////private static class RequestAjaxSearch {
	////	public String employeeName;
	////}

	//static
	@Override
	protected void onInitialize() {
		super.onInitialize();

		add(new AppPageCaption("page-caption", "素の jQuery ajax を利用したサンプル"));

		add(new AjaxHiddenPanel("ajax-search") {
			@Override
			public void onRequest() {

				try {
					RequestCycle requestCycle = getRequestCycle();

					String jsonString = AppUtils.getRequestString(requestCycle);

					// {"employeeName": "..."}

					//// with Open JSON
					////JSONObject queryJson = new JSONObject(jsonString);
					////String employeeName = queryJson.getString("employeeName");

					//// with Jackson: static class
					////ObjectMapper mapper = new ObjectMapper();
					////RequestAjaxSearch requestAjaxSearch = mapper.readValue(jsonString, RequestAjaxSearch.class);
					////String employeeName = requestAjaxSearch.employeeName;

					ObjectMapper mapper = new ObjectMapper();
					Map<String, String> requestMap = mapper.readValue(jsonString, new TypeReference<Map<String, String>>(){});
					String employeeName = requestMap.get("employeeName");

					String resultJsonString = "";
					try (EmployeeDao dao = new EmployeeDao()) {
						List<Employee> employees = dao.findEmployeesByName(employeeName);

						class Result {

							private String searchWord;
							private List<Employee> employeeInfo;

							public Result(String searchWord, List<Employee> employeeInfo) {
								this.searchWord = searchWord;
								this.employeeInfo = employeeInfo;
							}

							// NOTE: JSON 文字列化時に必要です。削除しないでください
							@SuppressWarnings("unused")
							public String getSearchWord() {
								return searchWord;
							}

							// NOTE: JSON 文字列化時に必要です。削除しないでください
							@SuppressWarnings("unused")
							public List<Employee> getEmployeeInfo() {
								return employeeInfo;
							}
						}

						resultJsonString = AppUtils.getJsonString("result", new Result(employeeName, employees));
					}

					requestCycle.scheduleRequestHandlerAfterCurrent(
							new TextRequestHandler("application/json", "UTF-8", resultJsonString)
						);

				} catch (Exception ex) {
					logger.error("{}", ex);
					throw new RuntimeException(ex);
				}
			}
		});
	}

}
