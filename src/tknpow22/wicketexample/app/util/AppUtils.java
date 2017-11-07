package tknpow22.wicketexample.app.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * アプリケーションで使用するユーティリティを定義する。
 */
public class AppUtils {

	/**
	 * リクエストされた値を文字列として取得する
	 * 素の jQuery ajax からの json 文字列を取得する際に使用する。
	 *
	 * @param requestCycle アプリケーションの保持する RequestCycle
	 * @return 受け取った文字列
	 * @throws IOException
	 */
	public static String getRequestString(RequestCycle requestCycle) throws IOException {
		WebRequest webRequest = (WebRequest) requestCycle.getRequest();
		HttpServletRequest request = (HttpServletRequest) webRequest.getContainerRequest();

		String resultString = null;

		try (
			ServletInputStream input = request.getInputStream();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
		) {
			byte[] buffer = new byte[4096];

			int length;
			while (0 < (length = input.read(buffer))) {
				output.write(buffer, 0, length);
			}
			output.flush();

			resultString = output.toString("UTF-8");
		}

		return resultString;
	}

	/**
	 * POJO から json 文字列を作成する
	 *
	 * @param fieldName フィールド名
	 * @param pojo 対応するオブジェクト
	 * @return json 文字列
	 * @throws IOException
	 */
	public static String getJsonString(String fieldName, Object pojo) throws IOException {

		String jsonString = null;

		JsonFactory jsonFactory = new JsonFactory();

		try (
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			JsonGenerator generator = jsonFactory.createGenerator(output);
		) {

			ObjectMapper mapper = new ObjectMapper();
			generator.setCodec(mapper);

			generator.writeStartObject();
			generator.writeObjectField(fieldName, pojo);
			generator.writeEndObject();

			generator.flush();
			output.flush();

			jsonString = output.toString("UTF-8");
		}

		return jsonString;
	}

	/**
	 * コンテキストパスからの URL パスを組み立てる
	 *
	 * @param app アプリケーションオブジェクト
	 * @param path パス
	 * @return コンテキストパスを含んだ URL パス
	 */
	public static String getContextUrl(WebApplication app, String path) {

		StringBuilder result = new StringBuilder();
		String contextPath = app.getServletContext().getContextPath();

		// コンテキストパスを "/contextPath/" の形式で設定する
		if (!contextPath.startsWith("/")) {
			result.append("/");
		}
		result.append(contextPath);
		if (!contextPath.endsWith("/")) {
			result.append("/");
		}

		// "path" を設定する
		if (path.startsWith("/")) {
			result.append(path.substring(1));
		} else {
			result.append(path);
		}

		return result.toString();
	}

	/**
	 * ログアウトの処理を行う
	 *
	 * @param component ページクラス
	 */
	public static void Logout(Component component) {
		AuthenticatedWebSession.get().invalidate();
		component.setResponsePage(component.getApplication().getHomePage());
	}

	/**
	 * Page クラスへのキャストを行う。
	 * SuppressWarnings("unchecked") の影響範囲を限定するために作成した。
	 *
	 * @param clazz Class<?>
	 * @return Class<Page>
	 */
	@SuppressWarnings("unchecked")
	public static Class<Page> castAsPage(Class<?> clazz) {
		return (Class<Page>) clazz;
	}
}
