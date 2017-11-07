package tknpow22.wicketexample.page.upload;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.PropertyModel;

import tknpow22.wicketexample.app.AppRoles;
import tknpow22.wicketexample.app.page.AppPageBase;
import tknpow22.wicketexample.app.parts.AppFeedbackPanel;
import tknpow22.wicketexample.app.parts.AppPageCaption;

/**
 * アップロード処理のサンプル
 */
@AuthorizeInstantiation({AppRoles.All})
public class Upload1 extends AppPageBase {

	@Override
	protected void onInitialize() {
		super.onInitialize();

		add(new AppPageCaption("page-caption", "アップロード１"));

		add(new UploadForm("uploadForm"));
	}

	/**
	 * アップロードフォームと処理を定義する。
	 */
	private class UploadForm extends Form<Void> {

		private FileUploadField fileupload;
		@SuppressWarnings("unused")
		private String contents = "";

		public UploadForm(String id) {
			super(id);
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();

			setMultiPart(true);

			{
				fileupload = new FileUploadField("fileupload");
				add(fileupload);
			}
			add(new Label("contents", new PropertyModel<String>(this, "contents")));

			add(new AppFeedbackPanel("feedbackPanel"));
		}


		@Override
		protected void onError() {
			contents = "";
		}

		@Override
		protected void onSubmit() {

			contents = "";

			FileUpload file = fileupload.getFileUpload();
			if (file == null) {
				error("ファイルを指定してください。");
			} else {
				int length = 100;

				byte[] uploadContents = file.getBytes();
				StringBuffer contentsString = new StringBuffer();
				for (int i = 0; i < uploadContents.length && i < length; ++i) {
					if (i != 0) {
						contentsString.append(",");
					}
					contentsString.append(String.format("0x%02X", uploadContents[i]));
				}
				if (length < uploadContents.length) {
					contentsString.append(",...");
				}
				//contents.set
				contents = contentsString.toString();
			}
		}
	}
}
