package tknpow22.wicketexample.page;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;

import tknpow22.wicketexample.app.page.AppWebPageBase;
import tknpow22.wicketexample.app.parts.AppFeedbackPanel;
import tknpow22.wicketexample.app.parts.AppPageCaption;
import tknpow22.wicketexample.dao.UserDao;

/**
 * ログインページ
 */
public class Login extends AppWebPageBase {

	@Override
	protected void onInitialize() {
		super.onInitialize();

		checkSignedIn = false;

		add(new AppPageCaption("page-caption", "ログイン"));

		add(new LoginForm("loginForm"));
	}

	/**
	 * ログインフォームと処理を定義する。
	 */
	private class LoginForm extends Form<Void> {
		private String userId = "";
		private String password = "";
		@SuppressWarnings("unused")
		private String focusId = "";
		@SuppressWarnings("unused")
		private String usersRoles = "";

		public LoginForm(String id) {
			super(id);
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();

			setDefaultModel(new CompoundPropertyModel<>(this));

			// usersRoles
			{
				StringBuffer buffer = new StringBuffer();
				try (UserDao dao = new UserDao()) {
					buffer.append("<ul>");
					List<Map<String, Object>> users = dao.findAllUsers();
					for (Map<String, Object> user : users) {
						String userId = (String) user.get("UserId");
						buffer.append(String.format("<li><span>%s</span>", userId));
						buffer.append("<ul>");
						List<Map<String, Object>> dRoles = dao.findRolesById(userId);
						for (Map<String, Object> dRole : dRoles) {
							String role = (String) dRole.get("Role");
							buffer.append(String.format("<li>%s</li>", role));
						}
						buffer.append("</ul>");
						buffer.append("</li>");
					}
					buffer.append("</ul>");
				}

				usersRoles = buffer.toString();
				add(new Label("usersRoles").setEscapeModelStrings(false));
			}

			add(new AppFeedbackPanel("feedbackPanel"));
			add(new TextField<>("userId"));
			add(new PasswordTextField("password").setRequired(false));
			add(new HiddenField<>("focusId"));
		}


		@Override
		protected void onError() {
			focusId = "userId";
		}

        @Override
        protected void onSubmit() {

			focusId = "userId";

			if (StringUtils.isEmpty(userId)) {
				error("ユーザーIDを入力してください。");
			} else if (StringUtils.isEmpty(password)) {
				error("パスワードを入力してください。");
				focusId = "password";
			} else {
				if (AuthenticatedWebSession.get().signIn(userId, password)) {
					success("認証しました。");
					setResponsePage(Menu.class);
				} else {
					error("ユーザーIDまたはパスワードを正しく入力してください。");
				}
			}
        }
	}
}
